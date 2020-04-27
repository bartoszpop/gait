package com.gmail.at.bartoszpop.gait.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Method;

import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
public final class TypeSafeRedoStrategyTest {
    @Test
    void apply_sameClassCommand_strategyApplies() {
        // given
        TypeSafeRedoStrategy<PlainCommand> strategy = new TypeSafeRedoStrategy<PlainCommand>() {
            @Override
            protected SupplierCommand<PlainCommand> applySafe(PlainCommand command, Method testMethod) {
                return () -> command;
            }
        };

        // when
        SupplierCommand<PlainCommand> redoCommand = strategy.apply(new PlainCommand(), new Object() {
        }.getClass()
                .getEnclosingMethod());

        // then
        assertThat(redoCommand, notNullValue());
    }

    @Test
    void apply_otherClassCommand_strategyAppliesNot() {
        // given
        TypeSafeRedoStrategy<PlainCommand> strategy = new TypeSafeRedoStrategy<PlainCommand>() {
            @Override
            protected SupplierCommand<PlainCommand> applySafe(PlainCommand command, Method testMethod) {
                return () -> command;
            }
        };

        // when
        SupplierCommand<InjecteeCommand> redoCommand = strategy.apply(new InjecteeCommand(null), new Object() {
        }.getClass()
                .getEnclosingMethod());

        // then
        assertThat(redoCommand, nullValue());
    }
}