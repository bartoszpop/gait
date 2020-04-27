package com.gmail.at.bartoszpop.gait.command;

import static java.lang.Boolean.TRUE;

import javax.inject.Inject;

import com.gmail.at.bartoszpop.gait.command.InjectableModule.Injectable;

/**
 * @author Bartosz Popiela
 */
final class InjecteeCommand implements StatefulCommand {

    private static InjecteeCommand instance;

    @Inject
    private Injectable injected;

    private Boolean redone;

    private volatile InjecteeCommand undoCommand;

    @Inject
    public InjecteeCommand(Injectable argument) {
        this.injected = argument;
        // Injectable is only instantiable in TestModule because of a private constructor
        if ( argument != null ) {
            InjecteeCommand.instance = this;
        }
    }

    /**
     * Returns the last instance that has its constructor arguments injected.
     */
    public static InjecteeCommand injecteeCommand() {
        return instance;
    }

    public Injectable getInjected() {
        return injected;
    }

    @Override
    public final void redo() {
        if ( redone != null ) {
            throw new IllegalStateException("This command has been already redone.");
        }
        redone = injected != null;
    }

    @Override
    public final InjecteeCommand undo() {
        // double-check idiom
        InjecteeCommand result = this.undoCommand;
        if ( result == null ) {
            synchronized (this) {
                result = this.undoCommand;
                if ( result == null ) {
                    this.undoCommand = result = new InjecteeCommand(null);
                }
            }
        }
        return result;
    }

    @Override
    public final boolean isRedone() {
        return TRUE.equals(redone);
    }
}