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
public final class SkipStrategyTest {
    @Test
    void apply_skipAny_sameClassCommand_supplierRedoesCommandNot() {
        // given
        PlainCommand command = new PlainCommand();
        SkipStrategy<PlainCommand> strategy = new SkipStrategy<>(PlainCommand.class);

        // when
        SupplierCommand<PlainCommand> redoCommand = strategy.apply(command, new Object() {
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
        SkipStrategy<PlainCommand> strategy = new SkipStrategy<>(PlainCommand.class);

        // when
        SupplierCommand<InjecteeCommand> redoCommand = strategy.apply(new InjecteeCommand(null), new Object() {
        }.getClass()
                .getEnclosingMethod());

        // then
        assertThat(redoCommand, nullValue());
    }

    @Test
    void apply_skipGiven_sameClassCommand_givenCommandSupplierRedoesCommandNot() {
        // given
        PlainCommand command = new PlainCommand();
        SkipStrategy<PlainCommand> strategy = new SkipStrategy<>(command);

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
    void apply_skipGiven_otherClassCommand_nullCommandSupplier() {
        // given
        PlainCommand command = new PlainCommand();
        SkipStrategy<PlainCommand> strategy = new SkipStrategy<>(command);

        // when
        SupplierCommand<InjecteeCommand> redoCommand = strategy.apply(new InjecteeCommand(null), new Object() {
        }.getClass()
                .getEnclosingMethod());

        // then
        assertThat(redoCommand, nullValue());
    }
}