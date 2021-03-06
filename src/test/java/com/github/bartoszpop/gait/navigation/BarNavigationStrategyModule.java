package com.github.bartoszpop.gait.navigation;

/**
 * @author Bartosz Popiela
 */
final class BarNavigationStrategyModule extends NavigationStrategyModule<FooPageObject> {
    @Override
    protected void createBindings() {
        bindNavigationStrategy().to(BarNavigationStrategy.class);
    }
}
