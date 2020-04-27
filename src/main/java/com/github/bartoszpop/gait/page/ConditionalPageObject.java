package com.github.bartoszpop.gait.page;

import javax.annotation.Nonnull;

/**
 * This is a page object that is to be used only if a defined condition has been met, that is the page object corresponds to the application state.
 *
 * @author Bartosz Popiela
 */
public interface ConditionalPageObject extends PageObject {

    /**
     * Creates a condition that, if met, this page object corresponds to the application state, e.g. it may be an element with a particular test locator
     * displayed.
     *
     * @return the condition for this page object
     * @see PageObjectConditionFactory
     */
    @Nonnull
    PageObjectCondition createCondition();
}