package com.gmail.at.bartoszpop.gait.page;

import static com.gmail.at.bartoszpop.gait.page.ConditionalPageObjectFactory.TimeoutLazyHolder.getDefaultTimeout;
import static com.gmail.at.bartoszpop.gait.page.ConditionalPageObjectFactory.TimeoutLazyHolder.getTimeoutMultiplier;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.format.DateTimeParseException;

import javax.annotation.Nonnull;

import org.awaitility.core.ConditionTimeoutException;

import lombok.extern.slf4j.Slf4j;

/**
 * This factory creates page objects which correspond to the application state, that is a condition defined by {@link ConditionalPageObject#createCondition()}
 * has been met.
 *
 * @author Bartosz Popiela
 */
@Slf4j
public abstract class ConditionalPageObjectFactory implements PageObjectFactory {

    /**
     * This property defines the default timeout to wait for the {@link ConditionalPageObject} in format accepted by {@link Duration#parse}. The default value
     * is 15 seconds.
     */
    public static final String DEFAULT_TIMEOUT_PROPERTY = "page.object.timeout.default";

    /**
     * This property defines the value to multiply the timeout to wait for {@link ConditionalPageObject}. The default value is 1.
     */
    public static final String TIMEOUT_MULTIPLIER_PROPERTY = "page.object.timeout.multiplier";

    /**
     * This method creates a page object of the given type and waits for the condition defined by the {@link ConditionalPageObject} to be met until the default
     * timeout elapsed.
     *
     * @return the page object
     * @throws IllegalStateException if the condition had not been met before the default time limit has been reached.
     */
    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    @Nonnull
    @Override
    public final <T> T create(@Nonnull Class<T> pageObjectClass) throws IllegalStateException {
        return create(pageObjectClass, getDefaultTimeout());
    }

    /**
     * This method creates a page object of the given type and waits for the condition defined by the {@link ConditionalPageObject} to be met until the timeout
     * elapsed. The timeout passed as the argument is multiplied by the {@value TIMEOUT_MULTIPLIER_PROPERTY} property.
     *
     * @return the page object
     * @throws IllegalStateException if the condition had not been met before the time limit has been reached.
     */
    @SuppressWarnings("squid:RedundantThrowsDeclarationCheck")
    @Nonnull
    public final <T> T create(Class<T> pageObjectClass, Duration timeoutMultiplicand) throws IllegalStateException {
        T pageObject = instantiate(pageObjectClass);
        waitOnCondition(pageObject, timeoutMultiplicand);
        return pageObject;
    }

    protected abstract <T> T instantiate(@Nonnull Class<T> pageObjectClass);

    private void waitOnCondition(Object pageObject, Duration timeoutMultiplicand) {
        if ( pageObject instanceof ConditionalPageObject ) {
            PageObjectCondition condition = ((ConditionalPageObject) pageObject).createCondition();
            Duration timeout = timeoutMultiplicand.multipliedBy(getTimeoutMultiplier());
            waitUntil(condition, timeout);
        }
    }

    private void waitUntil(PageObjectCondition condition, Duration timeout) {
        try {
            await().timeout(timeout)
                    .pollInterval(Duration.ofMillis(100))
                    .until(condition::met);
        } catch (ConditionTimeoutException ex) {
            throw new IllegalStateException(ex);
        }
    }

    static final class TimeoutLazyHolder {

        private static final Duration DEFAULT_TIMEOUT = createDefaultTimeout();

        private static final int TIMEOUT_MULTIPLIER = createTimeoutMultiplier();

        public static Duration getDefaultTimeout() {
            return DEFAULT_TIMEOUT;
        }

        public static int getTimeoutMultiplier() {
            return TIMEOUT_MULTIPLIER;
        }

        private static Duration createDefaultTimeout() {
            String defaultTimeoutProperty = System.getProperty(DEFAULT_TIMEOUT_PROPERTY);
            if ( defaultTimeoutProperty == null ) {
                return Duration.ofSeconds(15);
            } else {
                try {
                    return Duration.parse(defaultTimeoutProperty);
                } catch (DateTimeParseException ex) {
                    throw new IllegalArgumentException(DEFAULT_TIMEOUT_PROPERTY + " should be in a format defined by java.time.Duration.");
                }
            }
        }

        private static int createTimeoutMultiplier() {
            String timeoutMultiplierProperty = System.getProperty(TIMEOUT_MULTIPLIER_PROPERTY);
            if ( timeoutMultiplierProperty == null ) {
                return 1;
            } else {
                try {
                    return Integer.parseInt(timeoutMultiplierProperty);
                } catch (DateTimeParseException ex) {
                    throw new IllegalArgumentException(DEFAULT_TIMEOUT_PROPERTY + " should be an integer.");
                }
            }
        }
    }
}