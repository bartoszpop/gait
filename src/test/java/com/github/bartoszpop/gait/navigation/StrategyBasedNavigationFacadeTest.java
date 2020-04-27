package com.github.bartoszpop.gait.navigation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import javax.inject.Inject;

import org.testng.annotations.Guice;
import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
@Guice(modules = { FooNavigationFacadeModule.class, BarNavigationStrategyModule.class })
public final class StrategyBasedNavigationFacadeTest {

    @Inject
    private StrategyBasedNavigationFacade<FooPageObject> navigationFacade;

    @Test
    public void navigateTo_destinationPageObject() {
        // when
        BarPageObject bar = navigationFacade.navigateTo(BarPageObject.class);

        // then
        assertThat(bar, notNullValue());
    }

    @Test
    public void navigateBy_destinationPageObject() {
        // when
        BarPageObject bar = navigationFacade.navigateBy(BarNavigationStrategy.class);

        // then
        assertThat(bar, notNullValue());
    }
}