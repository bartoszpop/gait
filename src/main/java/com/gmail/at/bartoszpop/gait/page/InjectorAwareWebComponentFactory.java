package com.gmail.at.bartoszpop.gait.page;

import org.openqa.selenium.WebElement;

import com.github.webdriverextensions.WebComponent;
import com.github.webdriverextensions.internal.WebComponentFactory;
import com.gmail.at.bartoszpop.gait.inject.Injector;

/**
 * This factory instantiates web components with {@link Injector}.
 *
 * @author Bartosz Popiela
 */
final class InjectorAwareWebComponentFactory implements WebComponentFactory {

    private final Injector injector;

    public InjectorAwareWebComponentFactory(Injector injector) {
        this.injector = injector;
    }

    @Override
    public <T extends WebComponent> T create(Class<T> webComponentClass, WebElement webElement) {
        T instance = injector.getInstance(webComponentClass);
        instance.init(webElement);
        return instance;
    }
}