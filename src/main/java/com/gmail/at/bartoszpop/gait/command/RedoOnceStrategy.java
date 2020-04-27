package com.gmail.at.bartoszpop.gait.command;

import static com.gmail.at.bartoszpop.gait.command.RedoStrategy.nonRedoable;
import static com.gmail.at.bartoszpop.gait.command.RedoStrategy.redoable;

import java.lang.reflect.Method;

/**
 * This {@link RedoStrategy} redoes the given {@link Command} or a first {@link Command} of the given class once.
 *
 * @author Bartosz Popiela
 */
public class RedoOnceStrategy<T extends Command> extends TypeSafeRedoStrategy<T> {

    private T delegateCommand;

    private boolean commandRedone = false;

    /**
     * Constructs {@link RedoStrategy} that redoes a first {@link Command} of the given class once and returns this {@link Command} for any {@link Command} of
     * the same class.
     *
     * @param commandClass class to redo {@link Command} of
     */
    public RedoOnceStrategy(Class<T> commandClass) {
        super(commandClass);
    }

    /**
     * Constructs {@link RedoStrategy} that redoes the given {@link Command} once and returns this {@link Command} for any {@link Command} of the same class.
     *
     * @param delegateCommand {@link Command} to redo and return
     */
    public RedoOnceStrategy(T delegateCommand) {
        // Warning suppressed because the type argument must be any supertype of T
        // noinspection unchecked
        super((Class<T>) delegateCommand.getClass());
        this.delegateCommand = delegateCommand;
    }

    @Override
    protected final SupplierCommand<T> applySafe(T command, Method testMethod) {
        if ( delegateCommand == null ) {
            delegateCommand = command;
        }
        if ( false == commandRedone ) {
            commandRedone = true;
            return redoable(delegateCommand);
        }
        return nonRedoable(delegateCommand);
    }
}