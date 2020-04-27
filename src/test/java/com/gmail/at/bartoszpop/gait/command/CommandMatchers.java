package com.gmail.at.bartoszpop.gait.command;

import static org.hamcrest.Matchers.is;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

/**
 * @author Bartosz Popiela
 */
final class CommandMatchers {
    private CommandMatchers() {
    }

    public static Matcher<StatefulCommand> redone() {
        return new FeatureMatcher<StatefulCommand, Boolean>(is(true), "command redone", "redone") {
            @Override
            protected Boolean featureValueOf(StatefulCommand actual) {
                return actual.isRedone();
            }
        };
    }
}
