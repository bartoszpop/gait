package com.gmail.at.bartoszpop.gait.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
public final class SupplyOnceCommandTest {
    @Test
    public void get_redoTwice_suppliedOnce() {
        // given
        SupplyOnceCommand<Integer> command = new SupplyOnceCommand<Integer>() {
            private int value = 0;

            @Override
            protected Integer supply() {
                return value++;
            }
        };

        // when
        command.redo();
        command.redo();

        // then
        assertThat(command.get(), equalTo(0));
    }

    @Test
    public void get_getTwice_suppliedOnce() {
        // given
        SupplyOnceCommand<Integer> command = new SupplyOnceCommand<Integer>() {
            private int value = 0;

            @Override
            protected Integer supply() {
                return value++;
            }
        };

        // when
        command.redo();

        // then
        assertThat(command.get(), equalTo(0));
        assertThat(command.get(), equalTo(0));
    }
}