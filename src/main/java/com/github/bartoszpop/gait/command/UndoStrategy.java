package com.github.bartoszpop.gait.command;

import java.lang.reflect.Method;

/**
 * This is a strategy {@link CommandFacade#redo(Command)} delegates {@link Command Commands} to.
 *
 * <p>A valid implementation of this interface must have a public no-arg constructor.
 *
 * @author Bartosz Popiela
 */
public interface UndoStrategy {
    /**
     * Returns {@link Command} to undo the given {@link Command}.
     *
     * @param command {@link Command} to undo
     * @return the undo {@link Command}
     */
    Command apply(Command command, Method testMethod);

    final class Undo implements UndoStrategy {
        @Override
        public Command apply(Command command, Method testMethod) {
            return command.undo();
        }
    }

    final class NoOp implements UndoStrategy {
        @Override
        public Command apply(Command command, Method testMethod) {
            return Command.NO_OP;
        }
    }
}