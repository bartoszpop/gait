package com.github.bartoszpop.gait.command;

import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
public final class DescendingLockTest {

    private final DescendingLock lock = new DescendingLock();

    @Test
    void lock_objectLocked() throws InterruptedException {
        // given
        Object lockableObject = new Object();
        CountDownLatch lockLatch = new CountDownLatch(1);

        // when
        lock.lock(lockableObject);
        new Thread(() -> {
            try {
                lock.lock(lockableObject);
                lockLatch.countDown();
            } finally {
                lock.unlock();
            }
        }).start();

        // then
        assertFalse(lockLatch.await(100, MILLISECONDS));
    }

    @Test
    void lock_nextObjectLockedInPreviousObjectUnlockOrder() {
        // given
        Object previousObject = new Object();
        Object nextObject = new Object();
        List<Thread> unlockOrder = new ArrayList<>();
        List<Thread> lockOrder = new ArrayList<>();

        // when
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    lock.lock(previousObject);
                    unlockOrder.add(currentThread());
                    lock.lock(nextObject);
                    lockOrder.add(currentThread());
                } finally {
                    lock.unlock();
                }
            }).start();
        }

        // then
        assertThat(lockOrder, equalTo(unlockOrder));
    }

    @Test
    void lock_previousObjectUnlockedOnNextObjectLock() throws InterruptedException {
        // given
        Object previousObject = new Object();
        Object nextObject = new Object();
        CountDownLatch lockLatch = new CountDownLatch(1);

        // when
        lock.lock(previousObject);
        new Thread(() -> {
            try {
                lock.lock(previousObject);
                lockLatch.countDown();
            } finally {
                lock.unlock();
            }
        }).start();
        lock.lock(nextObject);
        lockLatch.await();

        // then
        assertThat(lockLatch.getCount(), equalTo(0L));
    }

    @Test
    void unlock_objectLocked_objectUnlocked() throws InterruptedException {
        // given
        Object lockableObject = new Object();
        CountDownLatch lockLatch = new CountDownLatch(1);

        // when
        lock.lock(lockableObject);
        new Thread(() -> {
            try {
                lock.lock(lockableObject);
                lockLatch.countDown();
            } finally {
                lock.unlock();
            }
        }).start();
        lock.unlock();
        lockLatch.await();

        // then
        assertThat(lockLatch.getCount(), equalTo(0L));
    }

    @AfterMethod
    private void unlock() {
        lock.unlock();
    }
}