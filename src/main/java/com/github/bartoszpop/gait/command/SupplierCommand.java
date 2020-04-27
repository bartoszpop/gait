package com.github.bartoszpop.gait.command;

import java.util.function.Supplier;

/**
 * This {@link Command} supplies a value of type {@code T}.
 *
 * @param <T> the type of value this {@link Command} supplies
 * @author Bartosz Popiela
 */
public interface SupplierCommand<T> extends Command, Supplier<T> {
    @Override
    default void redo() {
    }
}