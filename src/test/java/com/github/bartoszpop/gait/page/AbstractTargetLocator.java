package com.github.bartoszpop.gait.page;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;

/**
 * @author Bartosz Popiela
 */
abstract class AbstractTargetLocator implements TargetLocator {
    @Override
    public WebDriver frame(int index) {
        return null;
    }

    @Override
    public WebDriver frame(String nameOrId) {
        return null;
    }

    @Override
    public WebDriver frame(WebElement frameElement) {
        return null;
    }

    @Override
    public WebDriver parentFrame() {
        return null;
    }

    @Override
    public WebDriver window(String nameOrHandle) {
        return null;
    }

    @Override
    public WebDriver defaultContent() {
        return null;
    }

    @Override
    public WebElement activeElement() {
        return null;
    }

    @Override
    public Alert alert() {
        return null;
    }
}