package com.gmail.at.bartoszpop.gait.page;

import static java.time.Instant.now;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertThrows;

import java.time.Duration;
import java.time.Instant;

import javax.annotation.Nonnull;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.testng.annotations.Test;

import com.gmail.at.bartoszpop.gait.inject.SimpleInjector;

/**
 * @author Bartosz Popiela
 */
public final class ConditionalPageObjectFactoryTest {

    @Test
    public void create_reachDefaultTimeout_throwsException() {
        // given
        Instant beforeWait = now();
        ConditionalPageObjectFactory pageObjectFactory = new ConditionalPageObjectFactory() {
            @Override
            protected <T> T instantiate(@Nonnull Class<T> pageObjectClass) {
                return new SimpleInjector().getInstance(pageObjectClass);
            }
        };

        // then
        assertThrows(IllegalStateException.class, () -> pageObjectFactory.create(ConditionNotMetPageObject.class));
        assertThat(now(), isAfter(beforeWait.plus(getDefaultTimeout())));
    }

    @Test
    public void create_reachGivenTimeout_throwsException() {
        // given
        Instant beforeWait = now();
        Duration timeout = Duration.ofMillis(300);
        ConditionalPageObjectFactory pageObjectFactory = new ConditionalPageObjectFactory() {
            @Override
            protected <T> T instantiate(@Nonnull Class<T> pageObjectClass) {
                return new SimpleInjector().getInstance(pageObjectClass);
            }
        };

        // then
        assertThrows(IllegalStateException.class, () -> pageObjectFactory.create(ConditionNotMetPageObject.class, timeout));
        assertThat(now(), isAfter(beforeWait.plus(timeout)));
    }

    private Matcher<Instant> isAfter(Instant otherInstant) {
        return new TypeSafeMatcher<Instant>() {

            @Override
            protected boolean matchesSafely(Instant thisInstant) {
                return thisInstant.isAfter(otherInstant);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("was after ")
                        .appendValue(otherInstant);
            }
        };
    }

    private Duration getDefaultTimeout() {
        return Duration.parse(System.getProperty(ConditionalPageObjectFactory.DEFAULT_TIMEOUT_PROPERTY));
    }

    private static final class ConditionNotMetPageObject implements ConditionalPageObject {
        @Nonnull
        @Override
        public PageObjectCondition createCondition() {
            return () -> false;
        }
    }
}