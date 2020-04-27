package com.gmail.at.bartoszpop.gait.navigation;

import static org.testng.Assert.assertNotNull;

import javax.inject.Inject;

import org.testng.annotations.Guice;
import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
@Guice(modules = { FooNavigationFacadeModule.class, BarNavigationStrategyModule.class })
public final class StrategyBasedNavigationFacadeModuleTest {
    @Inject
    private NavigationFacade navigationFacade;

    @Inject
    private StrategyBasedNavigationFacade<FooPageObject> strategyBasedNavigationFacade;

    @Test
    public void bindNavigationFacade_facadeBound() {
        assertNotNull(navigationFacade);
        assertNotNull(strategyBasedNavigationFacade);
    }
}