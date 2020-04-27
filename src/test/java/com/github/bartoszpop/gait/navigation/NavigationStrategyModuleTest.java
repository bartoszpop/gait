package com.github.bartoszpop.gait.navigation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Set;

import javax.inject.Inject;

import org.testng.annotations.Guice;
import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
@Guice(modules = BarNavigationStrategyModule.class)
public final class NavigationStrategyModuleTest {
    @Inject
    private Set<NavigationStrategy<? super FooPageObject, ?>> navigationStrategies;

    @Test
    public void bindNavigationStrategy_strategyBound() {
        assertThat(navigationStrategies, notNullValue());
        assertThat(navigationStrategies, contains(instanceOf(BarNavigationStrategy.class)));
    }
}