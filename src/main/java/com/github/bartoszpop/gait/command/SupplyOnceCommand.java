package com.github.bartoszpop.gait.command;

import java.util.Optional;

/**
 * This {@link Command} supplies a value of type T while redone for a first time and returns this value from any subsequent {@link #get()} .
 *
 * @author Bartosz Popiela
 */
public abstract class SupplyOnceCommand<T> implements SupplierCommand<T> {

    // This field is intentionally volatile to comply with the double-check idiom and the null value states that SupplierCommand has not been redone yet
    @SuppressWarnings({ "squid:S3077", "OptionalUsedAsFieldOrParameterType" })
    private volatile Optional<T> value;

    // Warning suppressed because the null value states that SupplierCommand has not been redone yet
    @SuppressWarnings("squid:S2789")
    @Override
    public final void redo() {
        // noinspection OptionalAssignedToNull
        if ( value == null ) {
            synchronized (this) {
                // noinspection OptionalAssignedToNull
                if ( value == null ) {
                    value = Optional.ofNullable(supply());
                }
            }
        }
    }

    /**
     * Returns the supplied value of type T.
     *
     * @return the supplied value
     * @throws IllegalStateException if {@link Command} has not been redone
     */
    // Warning suppressed because the null value states that SupplierCommand has not been redone yet
    @SuppressWarnings("squid:S2789")
    @Override
    public final T get() {
        // noinspection OptionalAssignedToNull
        if ( value == null ) {
            synchronized (this) {
                // noinspection OptionalAssignedToNull
                if ( value == null ) {
                    throw new IllegalStateException("The command has not been redone.");
                }
            }
        }
        return value.orElse(null);
    }

    protected abstract T supply();
}
