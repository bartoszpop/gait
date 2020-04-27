package com.gmail.at.bartoszpop.gait.navigation;

/**
 * @author Bartosz Popiela
 */
final class BarNavigationStrategy implements NavigationStrategy<FooPageObject, BarPageObject> {
    @Override
    public BarPageObject navigateFrom(FooPageObject source) {
        return new BarPageObject();
    }
}
