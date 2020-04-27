package com.gmail.at.bartoszpop.gait.page;

import org.openqa.selenium.WebDriver;

/**
 * This factory returns {@link WebDriver} for the current thread to interact with.
 *
 * <p>In a multi-threaded test run different threads need separate {@link WebDriver WebDrivers} to navigate through their browser windows.
 * This is why {@link WebDriver} should not by accessed directly but with the factory {@link WebDriverFactory#getWebDriver() method}.
 *
 * @author Bartosz Popiela
 */
public interface WebDriverFactory {

    /**
     * Returns {@link WebDriver} for the current thread.
     *
     * @return {@link WebDriver}
     */
    WebDriver getWebDriver();
}