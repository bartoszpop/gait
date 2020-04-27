package com.github.bartoszpop.gait.command;

import static java.lang.Thread.currentThread;

import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.concurrent.ThreadSafe;

/**
 * This class acquires a lock for the given {@link Object} or enqueues the current thread if the lock has been already acquired by another thread.
 *
 * <p>A lock for {@link Object} is acquired in the same order in which threads have been enqueued for this lock. Once a thread acquires a lock it releases any
 * previously acquired lock. This <i>"unlock previous and lock next"</i> operation is performed in the atomic manner.
 *
 * @author Bartosz Popiela
 */
@ThreadSafe
final class DescendingLock {

    private final Map<Object, Queue<Thread>> lockableToLockQueue = new ConcurrentHashMap<>();

    private final ThreadLocal<Object> lockedObject = new ThreadLocal<>();

    public void lock(Object lockableObject) {
        synchronized (lockableObject) {
            unlock();
            enqueueLock(lockableObject);
            while (false == tryLock(lockableObject)) {
                try {
                    lockableObject.wait();
                } catch (InterruptedException e) {
                    currentThread().interrupt();
                    throw new LockFailedException(e);
                }
            }
            lockedObject.set(lockableObject);
        }
    }

    private void enqueueLock(Object lockableObject) {
        lockableToLockQueue.computeIfAbsent(lockableObject, anyLockable -> new LinkedList<>())
                .add(currentThread());
    }

    private boolean tryLock(Object lockableObject) {
        Queue<Thread> lockQueue = lockableToLockQueue.get(lockableObject);
        return Objects.equals(currentThread(), lockQueue.peek());
    }

    public void unlock() {
        Object lockableObject = lockedObject.get();
        if ( lockableObject != null ) {
            synchronized (lockableObject) {
                dequeueLock(lockableObject);
                lockableObject.notifyAll();
                lockedObject.remove();
            }
        }
    }

    private void dequeueLock(Object lockableObject) {
        Queue<Thread> lockQueue = lockableToLockQueue.get(lockableObject);
        lockQueue.remove();
        if ( lockQueue.isEmpty() ) {
            lockableToLockQueue.remove(lockableObject);
        }
    }

    private static final class LockFailedException extends RuntimeException {
        public LockFailedException(Throwable cause) {
            super(cause);
        }
    }
}