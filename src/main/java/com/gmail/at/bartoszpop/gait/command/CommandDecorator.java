package com.gmail.at.bartoszpop.gait.command;

/**
 * This {@link CompositeCommand} supplies {@link Command} it decorates.
 * <br>It may or may not redo a decorated {@link Command} as a delegate.
 *
 * @author Bartosz Popiela
 */
public abstract class CommandDecorator<T extends Command> extends CompositeCommand implements SupplierCommand<T> {
    private final T delegate;

    public CommandDecorator(T delegate) {
        this.delegate = delegate;
    }

    @Override
    public final T get() {
        return delegate;
    }
}