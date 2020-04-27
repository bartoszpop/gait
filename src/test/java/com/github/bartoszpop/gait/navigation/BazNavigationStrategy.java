package com.github.bartoszpop.gait.navigation;

/**
 * @author Bartosz Popiela
 */
final class BazNavigationStrategy implements NavigationStrategy<FooPageObject, BazPageObject> {
    @Override
    public BazPageObject navigateFrom(FooPageObject source) {
        return new BazPageObject();
    }
}
