package com.gmail.at.bartoszpop.gait.command;

import static com.gmail.at.bartoszpop.gait.command.Command.NO_OP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Method;

import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
public final class CompositeRedoStrategyTest {
    @Test
    void apply_firstStrategyApplies_secondStrategyApplies_firstStrategyApplied() {
        // given
        Command resultCommand = () -> {
        };
        CompositeRedoStrategy compositeStrategy = CompositeRedoStrategy.of(apply(resultCommand), apply(() -> {
        }));

        // when
        SupplierCommand<Command> redoCommand = compositeStrategy.apply(NO_OP, new Object() {
        }.getClass()
                .getEnclosingMethod());

        // then
        assertThat(redoCommand, notNullValue());
        assertThat(redoCommand.get(), equalTo(resultCommand));
    }

    @Test
    void apply_firstStrategyAppliesNot_secondStrategyApplies_secondStrategyApplied() {
        // given
        Command resultCommand = () -> {
        };
        CompositeRedoStrategy compositeStrategy = CompositeRedoStrategy.of(applyNot(), apply(resultCommand));

        // when
        SupplierCommand<Command> redoCommand = compositeStrategy.apply(NO_OP, new Object() {
        }.getClass()
                .getEnclosingMethod());

        // thennotNullValue
        assertThat(redoCommand, notNullValue());
        assertThat(redoCommand.get(), equalTo(resultCommand));
    }

    @Test
    void apply_noneStrategyApplies_noneStrategyApplied() {
        // given
        CompositeRedoStrategy compositeStrategy = CompositeRedoStrategy.of(applyNot(), applyNot());

        // when
        SupplierCommand<Command> redoCommand = compositeStrategy.apply(NO_OP, new Object() {
        }.getClass()
                .getEnclosingMethod());

        // then
        assertThat(redoCommand, nullValue());
    }

    private RedoStrategy applyNot() {
        return new RedoStrategy() {
            @Override
            public <T extends Command> SupplierCommand<T> apply(T command, Method testMethod) {
                return null;
            }
        };
    }

    private RedoStrategy apply(Command redoCommand) {
        return new RedoStrategy() {
            @Override
            public <T extends Command> SupplierCommand<T> apply(T command, Method testMethod) {
                // Warning suppressed because the type argument is Command solely
                //noinspection unchecked
                return () -> (T) redoCommand;
            }
        };
    }
}