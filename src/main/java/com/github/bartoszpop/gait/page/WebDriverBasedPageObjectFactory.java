package com.github.bartoszpop.gait.page;

import static java.util.Arrays.stream;
import static java.util.Collections.synchronizedMap;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.github.bartoszpop.gait.inject.Injector;
import com.github.bartoszpop.gait.inject.SimpleInjector;
import com.github.webdriverextensions.WebDriverExtensionFieldDecorator;
import com.github.webdriverextensions.internal.DefaultWebComponentListFactory;
import com.github.webdriverextensions.internal.WebComponentFactory;
import com.github.webdriverextensions.internal.WebComponentListFactory;

/**
 * This factory injects {@link WebElement WebElements} to page objects with {@link PageFactory}.
 *
 * @author Bartosz Popiela
 */
public final class WebDriverBasedPageObjectFactory extends ConditionalPageObjectFactory implements FocusablePageObjectFactory {

    private static final Map<Object, String> pageObjectToWindow = synchronizedMap(new WeakHashMap<>());

    private final WebDriverFactory webDriverFactory;

    private Injector injector = new SimpleInjector();

    @Inject
    public WebDriverBasedPageObjectFactory(WebDriverFactory webDriverFactory) {
        this.webDriverFactory = webDriverFactory;
    }

    @Inject
    private void setInjector(Injector injector) {
        this.injector = injector;
    }

    @Override
    protected <T> T instantiate(@Nonnull Class<T> pageObjectClass) {
        T pageObject = injector.getInstance(pageObjectClass);
        WebDriver webDriver = webDriverFactory.getWebDriver();
        pageObjectToWindow.put(pageObject, webDriver.getWindowHandle());
        PageFactory.initElements(createFieldDecorator(webDriver), pageObject);
        return pageObject;
    }

    // XXX this is a workaround to instantiate WebElements with Injector
    private WebDriverExtensionFieldDecorator createFieldDecorator(WebDriver webDriver) {
        WebDriverExtensionFieldDecorator fieldDecorator = new WebDriverExtensionFieldDecorator(webDriver);
        WebComponentFactory webComponentFactory = new InjectorAwareWebComponentFactory(injector);
        overwrite(fieldDecorator, WebComponentFactory.class, webComponentFactory);
        overwrite(fieldDecorator, WebComponentListFactory.class, new DefaultWebComponentListFactory(webComponentFactory));
        return fieldDecorator;
    }

    private <T> void overwrite(Object target, Class<T> fieldType, T value) {
        Field field = findField(target.getClass(), fieldType);
        synchronized (field) {
            boolean fieldAccessible = field.isAccessible();
            try {
                if ( fieldAccessible == false ) {
                    field.setAccessible(true);
                }
                field.set(target, value);
            } catch (IllegalAccessException ex) {
                throw new FieldOverwriteException("Could not overwrite " + fieldType.getSimpleName() + ".", ex);
            } finally {
                field.setAccessible(fieldAccessible);
            }
        }
    }

    private <T> Field findField(Class<?> type, Class<T> fieldType) {
        return stream(type.getDeclaredFields()).filter(field -> fieldType.equals(field.getType()))
                .findFirst()
                .orElseThrow(() -> new FieldOverwriteException("A field of type " + fieldType.getSimpleName() + " not found."));
    }

    @Nonnull
    @Override
    public <T> T focus(@Nonnull T pageObject) {
        String windowHandle = pageObjectToWindow.get(pageObject);
        WebDriver webDriver = webDriverFactory.getWebDriver();
        webDriver.switchTo()
                .window(windowHandle);
        return pageObject;
    }

    private static final class FieldOverwriteException extends RuntimeException {
        public FieldOverwriteException(String message) {
            super(message);
        }

        public FieldOverwriteException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
