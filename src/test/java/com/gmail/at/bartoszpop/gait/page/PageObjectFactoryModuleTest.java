package com.gmail.at.bartoszpop.gait.page;

import static com.gmail.at.bartoszpop.gait.page.PageObjectFactoryModuleTest.WebDriverBasedPageObjectFactoryModule;
import static org.testng.Assert.assertNotNull;

import javax.inject.Inject;

import org.testng.annotations.Guice;
import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
@Guice(modules = WebDriverBasedPageObjectFactoryModule.class)
public final class PageObjectFactoryModuleTest {

    @Inject
    private PageObjectFactory pageObjectFactory;

    @Inject
    private FocusablePageObjectFactory focusablePageObjectFactory;

    @Inject
    private ConditionalPageObjectFactory conditionalPageObjectFactory;

    @Test
    public void bindPageObjectFactory_conditionalPageObjectFactoryBound() {
        assertNotNull(conditionalPageObjectFactory);
    }

    @Test
    public void bindPageObjectFactory_focusablePageObjectFactoryBound() {
        assertNotNull(focusablePageObjectFactory);
    }

    @Test
    public void bindPageObjectFactory_pageObjectFactoryBound() {
        assertNotNull(pageObjectFactory);
    }

    static final class WebDriverBasedPageObjectFactoryModule extends PageObjectFactoryModule<WebDriverBasedPageObjectFactory> {
        @Override
        protected void createBindings() {
            bind(WebDriverFactory.class).toInstance(() -> new AbstractWebDriver() {
            });
        }
    }
}