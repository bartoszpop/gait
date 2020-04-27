package com.gmail.at.bartoszpop.gait.command;

import static com.gmail.at.bartoszpop.gait.command.RedoStrategy.nonRedoable;

import java.lang.reflect.Method;

/**
 * This strategy skips any {@link Command} of the given class or the same class as the given {@link Command}.
 *
 * @author Bartosz Popiela
 */
public final class SkipStrategy<T extends Command> extends TypeSafeRedoStrategy<T> {

    private T delegateCommand;

    /**
     * Constructs {@link RedoStrategy} that skips and returns any {@link Command} of the given class.
     *
     * @param commandClass class to skip {@link Command Commands} of
     */
    public SkipStrategy(Class<T> commandClass) {
        super(commandClass);
    }

    /**
     * Constructs {@link RedoStrategy} that skips any {@link Command} of the same class as the given {@link Command} and returns the given {@link Command}.
     *
     * @param delegateCommand {@link Command} to return
     */
    public SkipStrategy(T delegateCommand) {
        // Warning suppressed because the type argument must be any supertype of T
        // noinspection unchecked
        super((Class<T>) delegateCommand.getClass());
        this.delegateCommand = delegateCommand;
    }

    @Override
    protected SupplierCommand<T> applySafe(T command, Method testMethod) {
        return nonRedoable(delegateCommand != null ? delegateCommand : command);
    }
}