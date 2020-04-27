package com.gmail.at.bartoszpop.gait.page;

import static java.util.Optional.empty;

import java.time.Duration;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * This page object should be used in case the UI action may redirect to different destination locations. In opposite to {@link UndefinedPageObject}, this page
 * object defines a condition for the destination page.
 *
 * <p>It should be used only in exceptional circumstances. A test scenario should not depend on any condition and the {@link UndefinedPageObject#expect} method
 * should be used in preference to this method.
 *
 * @author Bartosz Popiela
 */
public class OptionalPageObject<T> implements ConditionalPageObject {

    @Inject
    private ConditionalPageObjectFactory pageObjectFactory;

    /**
     * This method attempts to create a page object instance of the given class and check if the condition defined by {@link ConditionalPageObject} is met.
     *
     * @param <S> the type of the page object to create
     * @param expectedClass the {@link Class} object corresponding to the page object type
     * @return the page object instance
     */
    @Nonnull
    public final <S extends T> Optional<S> maybe(@Nonnull Class<S> expectedClass) {
        try {
            return Optional.of(pageObjectFactory.create(expectedClass, Duration.ZERO));
        } catch (IllegalStateException ex) {
            return empty();
        }
    }

    @Nonnull
    @Override
    public PageObjectCondition createCondition() {
        return () -> true;
    }
}