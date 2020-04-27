package com.github.bartoszpop.gait.command;

import java.lang.reflect.Method;

import javax.annotation.concurrent.ThreadSafe;

/**
 * This is a strategy {@link CommandFacade#redo(Command)} delegates {@link Command Commands} to.
 *
 * <p>A valid implementation of this interface must be thread-safe and have a public no-arg constructor.
 *
 * @author Bartosz Popiela
 * @see CommandFacade
 */
@ThreadSafe
public interface RedoStrategy {

    /**
     * Returns {@link SupplierCommand} that redoes the supplied {@link Command} once redone.
     *
     * @param command {@link Command} to supply
     * @param <T> the type of {@link Command}
     * @return a {@link Command} supplier
     */
    static <T extends Command> SupplierCommand<T> redoable(T command) {
        return new SingleCommand<T>() {
            @Override
            protected T createDelegate() {
                return command;
            }

            @Override
            public String toString() {
                return "Redo[" + get() + "]";
            }
        };
    }

    /**
     * Returns {@link SupplierCommand} that doesn't redo the supplied {@link Command}.
     *
     * @param command {@link Command} to supply
     * @param <T> the type of {@link Command}
     * @return a {@link Command} supplier
     */
    static <T extends Command> SupplierCommand<T> nonRedoable(T command) {
        return new SupplierCommand<T>() {
            @Override
            public T get() {
                return command;
            }

            @Override
            public String toString() {
                return "NoOp[" + get() + "]";
            }
        };
    }

    /**
     * Returns {@link SupplierCommand} to redo that supplies the result {@link Command}.
     *
     * @param command the expected {@link Command}
     * @param testMethod a test method the current thread is in or is to invoke
     * @param <T> the type of {@link Command}
     * @return the result {@link Command}
     */
    <T extends Command> SupplierCommand<T> apply(T command, Method testMethod);

    /**
     * This strategy redoes any {@link Command}.
     */
    class Redo implements RedoStrategy {
        @Override
        public <T extends Command> SupplierCommand<T> apply(T command, Method testMethod) {
            return new SingleCommand<T>() {
                @Override
                protected T createDelegate() {
                    return command;
                }
            };
        }
    }
}