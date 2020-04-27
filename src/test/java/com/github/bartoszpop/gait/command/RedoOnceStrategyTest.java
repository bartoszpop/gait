package com.github.bartoszpop.gait.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
public final class RedoOnceStrategyTest {
    @Test
    void apply_redoOnceAny_firstSameClassCommand_firstCommandSupplierRedoesCommand() {
        // given
        PlainCommand command = new PlainCommand();
        RedoOnceStrategy<PlainCommand> strategy = new RedoOnceStrategy<>(PlainCommand.class);

        // when
        SupplierCommand<PlainCommand> redoCommand = strategy.apply(command, new Object() {
        }.getClass()
                .getEnclosingMethod());
        redoCommand.redo();

        // then
        PlainCommand resultCommand = redoCommand.get();
        assertThat(resultCommand, equalTo(command));
        MatcherAssert.assertThat(resultCommand, CommandMatchers.redone());
    }

    @Test
    void apply_redoOnceAny_nextSameClassCommand_firstCommandSupplierRedoesCommandNot() {
        // given
        PlainCommand command = new PlainCommand();
        RedoOnceStrategy<PlainCommand> strategy = new RedoOnceStrategy<>(PlainCommand.class);
        strategy.apply(command, new Object() {
        }.getClass()
                .getEnclosingMethod());

        // when
        SupplierCommand<PlainCommand> redoCommand = strategy.apply(new PlainCommand(), new Object() {
        }.getClass()
                .getEnclosingMethod());
        redoCommand.redo();

        // then
        PlainCommand resultCommand = redoCommand.get();
        assertThat(resultCommand, equalTo(command));
        MatcherAssert.assertThat(resultCommand, Matchers.not(CommandMatchers.redone()));
    }

    @Test
    void apply_redoOnceAny_otherClassCommand_nullCommandSupplier() {
        // given
        RedoOnceStrategy<PlainCommand> strategy = new RedoOnceStrategy<>(PlainCommand.class);

        // when
        SupplierCommand<InjecteeCommand> redoCommand = strategy.apply(new InjecteeCommand(null), new Object() {
        }.getClass()
                .getEnclosingMethod());

        // then
        assertThat(redoCommand, nullValue());
    }

    @Test
    void apply_redoOnceGiven_firstSameClassCommand_givenCommandSupplierRedoesCommand() {
        // given
        PlainCommand command = new PlainCommand();
        RedoOnceStrategy<PlainCommand> strategy = new RedoOnceStrategy<>(command);

        // when
        SupplierCommand<PlainCommand> redoCommand = strategy.apply(new PlainCommand(), new Object() {
        }.getClass()
                .getEnclosingMethod());
        redoCommand.redo();

        // then
        PlainCommand resultCommand = redoCommand.get();
        assertThat(resultCommand, equalTo(command));
        MatcherAssert.assertThat(resultCommand, CommandMatchers.redone());
    }

    @Test
    void apply_redoOnceGiven_nextSameClassCommand_givenCommandSupplierRedoesCommandNot() {
        // given
        PlainCommand command = new PlainCommand();
        RedoOnceStrategy<PlainCommand> strategy = new RedoOnceStrategy<>(command);
        strategy.apply(new PlainCommand(), new Object() {
        }.getClass()
                .getEnclosingMethod());

        // when
        SupplierCommand<PlainCommand> redoCommand = strategy.apply(new PlainCommand(), new Object() {
        }.getClass()
                .getEnclosingMethod());
        redoCommand.redo();

        // then
        PlainCommand resultCommand = redoCommand.get();
        assertThat(resultCommand, equalTo(command));
        MatcherAssert.assertThat(resultCommand, Matchers.not(CommandMatchers.redone()));
    }

    @Test
    void apply_redoOnceGiven_otherClassCommand_nullCommandSupplier() {
        // given
        RedoOnceStrategy<PlainCommand> strategy = new RedoOnceStrategy<>(new PlainCommand());

        // when
        SupplierCommand<InjecteeCommand> redoCommand = strategy.apply(new InjecteeCommand(null), new Object() {
        }.getClass()
                .getEnclosingMethod());

        // then
        assertThat(redoCommand, nullValue());
    }
}