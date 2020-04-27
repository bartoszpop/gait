package com.gmail.at.bartoszpop.gait.navigation;

/**
 * @author Bartosz Popiela
 */
final class FooNavigationFacade extends StrategyBasedNavigationFacade<FooPageObject> {
    @Override
    protected FooPageObject navigateToReferenceComponent() {
        return new FooPageObject();
    }
}