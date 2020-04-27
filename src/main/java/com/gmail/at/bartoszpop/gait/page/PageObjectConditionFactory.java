package com.gmail.at.bartoszpop.gait.page;

import java.time.Duration;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

/**
 * This class contains factory methods for handy conditions.
 *
 * @author Bartosz Popiela
 */
public final class PageObjectConditionFactory {

    private final WebDriverFactory webDriverFactory;

    private final WebDriverBasedPageObjectFactory pageObjectFactory;

    @Inject
    public PageObjectConditionFactory(WebDriverFactory webDriverFactory, WebDriverBasedPageObjectFactory pageObjectFactory) {
        this.webDriverFactory = webDriverFactory;
        this.pageObjectFactory = pageObjectFactory;
    }

    public PageObjectCondition is(Class<?> pageObjectClass) {
        return () -> {
            try {
                // Warning suppressed because this method may throw an exception
                //noinspection ConstantConditions
                return null != pageObjectFactory.create(pageObjectClass, Duration.ZERO);
            } catch (IllegalStateException ex) {
                return false;
            }
        };
    }

    public PageObjectCondition isReady() {
        return safe(webDriver -> {
            JavascriptExecutor js = (JavascriptExecutor) webDriverFactory.getWebDriver();
            String pageState = (String) js.executeScript("return document.readyState");
            return pageState.equals("complete");
        });
    }

    public <T> PageObjectCondition element(T element, Predicate<? super T> elementCondition) {
        return safe(webDriver -> elementCondition.test(element));
    }

    public PageObjectCondition element(By element, Predicate<? super WebElement> elementCondition) {
        return safe(webDriver -> elementCondition.test(webDriver.findElement(element)));
    }

    public PageObjectCondition elementFound(By element) {
        return safe(webDriver -> !webDriver.findElements(element)
                .isEmpty());
    }

    private PageObjectCondition safe(Predicate<WebDriver> predicate) {
        return () -> {
            try {
                return predicate.test(webDriverFactory.getWebDriver());
            } catch (WebDriverException ex) {
                return false;
            }
        };
    }
}