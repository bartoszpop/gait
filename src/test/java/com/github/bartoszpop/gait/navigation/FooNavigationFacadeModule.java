package com.github.bartoszpop.gait.navigation;

/**
 * @author Bartosz Popiela
 */
final class FooNavigationFacadeModule extends StrategyBasedNavigationFacadeModule<FooPageObject> {
    @Override
    protected void createBindings() {
        bindNavigationFacade().to(FooNavigationFacade.class);
    }
}
