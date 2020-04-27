package com.gmail.at.bartoszpop.gait.page;

import javax.annotation.Nonnull;

/**
 * This is a factory to create page objects.
 *
 * @author Bartosz Popiela
 */
public interface PageObjectFactory {
    /**
     * This method creates a page object of the given type.
     *
     * @param <T> the type of the page object to create
     * @param pageObjectClass {@link Class} corresponding to the page object type
     * @return the page object
     */
    @Nonnull
    <T> T create(@Nonnull Class<T> pageObjectClass);
}