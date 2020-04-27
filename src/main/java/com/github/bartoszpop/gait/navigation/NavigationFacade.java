package com.github.bartoszpop.gait.navigation;

/**
 * This interface provides an API to navigate through the underlying UI structure.
 *
 * <p>It decouples the navigation steps from the integration test steps.
 *
 * <p>This facade uses the terminology defined in the Page Object design pattern. In particular, the "page object" term may refer to not only the entire web
 * page but any UI fragment in any UI technology.
 *
 * @author Bartosz Popiela
 * @apiNote The {@link StrategyBasedNavigationFacade} is a reference implementation of this interface.
 * @see <a href="https://martinfowler.com/bliki/PageObject.html">https://martinfowler.com/bliki/PageObject.html</a>
 */
public interface NavigationFacade {

    /**
     * This method navigates to the page object of the given type.
     *
     * @param <T> the type of the page object to navigate to
     * @param destinationType the {@link Class} object corresponding to the page object type
     * @return the destination page object
     */
    <T> T navigateTo(Class<T> destinationType);
}
