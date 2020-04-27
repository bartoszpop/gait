package com.github.bartoszpop.gait.command;

import static java.util.Collections.singletonList;

import java.util.List;

/**
 * This {@link CompositeCommand} consists of a single {@link Command},
 *
 * @author Bartosz Popiela
 */
public abstract class SingleCommand<T extends Command> extends CompositeCommand implements SupplierCommand<T> {
    @Override
    protected final List<Command> createDelegates() {
        return singletonList(createDelegate());
    }

    protected abstract T createDelegate();

    @Override
    public Command undo() {
        return get().undo();
    }

    public final T get() {
        // warning suppressed because the delegate command is of type T
        //noinspection unchecked
        return (T) getDelegates().iterator()
                .next();
    }
}