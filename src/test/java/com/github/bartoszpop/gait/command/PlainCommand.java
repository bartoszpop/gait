package com.github.bartoszpop.gait.command;

import static java.lang.Boolean.TRUE;

/**
 * @author Bartosz Popiela
 */
final class PlainCommand implements StatefulCommand {

    private static PlainCommand instance;

    private Boolean redone;

    {
        instance = this;
    }

    /**
     * Returns the last instance of this class.
     */
    public static PlainCommand plainCommand() {
        return instance;
    }

    @Override
    public final void redo() {
        if ( redone != null ) {
            throw new IllegalStateException("This command has been already redone.");
        }
        redone = true;
    }

    @Override
    public Command undo() {
        // This is intentionally not PlainCommand not to overwrite PlainCommand#instance.
        return NO_OP;
    }

    @Override
    public final boolean isRedone() {
        return TRUE.equals(redone);
    }
}