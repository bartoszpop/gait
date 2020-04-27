package com.github.bartoszpop.gait.command;

import static com.github.bartoszpop.gait.command.CommandListener.getCurrentContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * This class supplies a value scoped to a test method.
 * <br>If a value is an instance of {@link Command} it will be redone.
 *
 * @author Bartosz Popiela
 * @see Fixturable
 */
public final class Fixture<T> {

    private final Map<FixtureContext, T> contextToValue = new ConcurrentHashMap<>();

    private final Supplier<T> valueSupplier;

    private Fixture<T> delegate = this;

    public Fixture(Supplier<T> valueSupplier) {
        this.valueSupplier = valueSupplier;
    }

    void redo(CommandFacade commandFacade, FixtureContext fixtureContext) {
        T value = valueSupplier.get();
        if ( value instanceof Command ) {
            // Warning suppressed because T is a subtype of Command
            // noinspection unchecked
            contextToValue.put(fixtureContext, (T) commandFacade.redo((Command) value));
        } else {
            contextToValue.put(fixtureContext, value);
        }
    }

    void setDelegate(Fixture<T> fixture) {
        this.delegate = fixture;
    }

    /**
     * Returns the supplied value scoped to the current test method, that means a test method the current thread is in or is to invoke.
     *
     * @return the supplied value
     * @throws IllegalStateException if no value has been supplied
     */
    public T get() {
        return delegate.findValueByContext(getCurrentContext());
    }

    private T findValueByContext(FixtureContext currentContext) {
        FixtureContext context = currentContext;
        while (context != null) {
            T value = contextToValue.get(context);
            if ( value != null ) {
                return value;
            }
            context = context.toOuterContext();
        }
        throw new IllegalStateException("No value has been supplied.");
    }
}