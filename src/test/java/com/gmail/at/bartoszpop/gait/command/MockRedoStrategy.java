package com.gmail.at.bartoszpop.gait.command;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @author Bartosz Popiela
 */
final class MockRedoStrategy implements RedoStrategy {

    private static final Function<Command, SupplierCommand<Command>> NONE = (command) -> null;

    private static final Function<Command, SupplierCommand<Command>> ANY = (command) -> new SingleCommand<Command>() {
        @Override
        protected Command createDelegate() {
            return command;
        }
    };

    private static volatile Function<Command, SupplierCommand<Command>> delegate = ANY;

    public void redoAny() {
        delegate = ANY;
    }

    public void redoNone() {
        delegate = NONE;
    }

    public void redo(Command command) {
        delegate = (anyCommand) -> new SingleCommand<Command>() {
            @Override
            protected Command createDelegate() {
                return command;
            }
        };
    }

    public void apply(Function<Command, SupplierCommand<Command>> apply) {
        delegate = apply;
    }

    @Override
    public <T extends Command> SupplierCommand<T> apply(T command, Method testMethod) {
        // Warning suppressed because the redo command is always of type T
        // noinspection unchecked
        return (SupplierCommand<T>) delegate.apply(command);
    }
}