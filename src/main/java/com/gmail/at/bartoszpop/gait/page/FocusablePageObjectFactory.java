package com.gmail.at.bartoszpop.gait.page;

import javax.annotation.Nonnull;

/**
 * This factory sets focus on the window that had focus set when the given page object was created.
 *
 * @author Bartosz Popiela
 */
public interface FocusablePageObjectFactory extends PageObjectFactory {
    /**
     * Sets focus on the page object window.
     *
     * @param pageObject the page object to set focus on its window
     * @param <T> the type of the page object
     * @return the page object given as an argument
     */
    @Nonnull
    <T> T focus(@Nonnull T pageObject);
}