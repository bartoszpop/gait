package com.gmail.at.bartoszpop.gait.navigation;

/**
 * A strategy for navigating from the page object of type S to the page object of type T.
 *
 * @param <T> the type of the page object this strategy navigates from
 * @param <S> the type of the page object this strategy navigates to
 * @author Bartosz Popiela
 * @apiNote There may exist multiple implementations for the same source and destination type pair.
 * @see StrategyBasedNavigationFacade
 */
public interface NavigationStrategy<T, S> {

    /**
     * This method navigates from the given page object to the destination page object.
     *
     * @param source the page object to start navigation from
     * @return the destination page object
     * @implSpec This method should not reference the {@link NavigationFacade} because it may lead to the invocation cycle.
     */
    S navigateFrom(T source);
}
