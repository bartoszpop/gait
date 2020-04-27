package com.gmail.at.bartoszpop.gait.command;

/**
 * @author Bartosz Popiela
 */
final class NoOpLoginCommand extends OrderedCommand implements LoginCommand {
    private static NoOpLoginCommand instance;

    {
        instance = this;
    }

    public NoOpLoginCommand() {
        super(LoginCommand.class.getSimpleName());
    }

    /**
     * Returns the last instance of this class.
     */
    public static OrderedCommand loginCommand() {
        return instance;
    }
}
