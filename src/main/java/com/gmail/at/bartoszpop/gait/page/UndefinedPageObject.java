package com.gmail.at.bartoszpop.gait.page;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * This page object should be used in case the UI action may redirect to different destination locations.
 *
 * @author Bartosz Popiela
 */
@SuppressWarnings({ "WeakerAccess", "unused" })
public class UndefinedPageObject<T> implements PageObject {

    @Inject
    private PageObjectFactory pageObjectFactory;

    /**
     * This method attempts to create a page object instance of the given class and throws an exception if fails.
     *
     * @param <S> the type of the page object to create
     * @param expectedClass the {@link Class} object corresponding to the page object type
     * @return the page object instance
     * @throws IllegalStateException if failed to create the expected page object
     */
    @Nonnull
    public final <S extends T> S expect(@Nonnull Class<S> expectedClass) {
        try {
            return pageObjectFactory.create(expectedClass);
        } catch (IllegalStateException ex) {
            throw new IllegalStateException(expectedClass.getName() + " expected.", ex);
        }
    }
}