package com.github.bartoszpop.gait.command;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * This interface represents an action and comply with the Command design pattern.
 *
 * @author Bartosz Popiela
 */
@NotThreadSafe
public interface Command {
    Command NO_OP = new Command() {
        @Override
        public void redo() {
        }

        @Override
        public Command undo() {
            return this;
        }

        @Override
        public String toString() {
            return "NO_OP";
        }
    };

    static Command of(Command redoCommand, Command undoCommand) {
        return new Command() {
            @Override
            public void redo() {
                redoCommand.redo();
            }

            @Override
            public Command undo() {
                return undoCommand;
            }

            @Override
            public String toString() {
                return getClass().getName() + "(redo=" + redoCommand + ", undo=" + undoCommand + ")";
            }
        };
    }

    /**
     * Redoes this command.
     *
     * <p>This method should not be invoked directly, {@link CommandFacade#redo(Command)} should be used instead.
     */
    void redo();

    /**
     * Returns {@link Command} that undo this {@link Command}.
     *
     * <p>This method should not be invoked directly, {@link CommandFacade#redo(Command)} should be used instead.
     *
     * @return the undo command
     */
    default Command undo() {
        return NO_OP;
    }
}