package com.gmail.at.bartoszpop.gait.command;

import java.lang.reflect.Method;
import java.util.function.UnaryOperator;

/**
 * @author Bartosz Popiela
 */
final class MockUndoStrategy implements UndoStrategy {

    private static final UnaryOperator<Command> NONE = command -> null;

    private static final UnaryOperator<Command> ANY = Command::undo;

    private static UnaryOperator<Command> undoStrategy = ANY;

    public void undoNone() {
        undoStrategy = NONE;
    }

    public void undoAny() {
        undoStrategy = ANY;
    }

    @Override
    public Command apply(Command command, Method testMethod) {
        return undoStrategy.apply(command);
    }
}