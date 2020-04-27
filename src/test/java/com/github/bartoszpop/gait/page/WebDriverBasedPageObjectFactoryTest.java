package com.github.bartoszpop.gait.page;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
public final class WebDriverBasedPageObjectFactoryTest {
    @Test
    public void create_pageObjectHasWebElementsSet() {
        // given
        String username = "John Doe";
        WebDriver webDriver = new AbstractWebDriver() {
            @Override
            public WebElement findElement(By by) {
                return new AbstractWebElement() {
                    @Override
                    public String getText() {
                        return username;
                    }
                };
            }
        };
        WebDriverBasedPageObjectFactory pageObjectFactory = new WebDriverBasedPageObjectFactory(() -> webDriver);

        // when
        UserProfilePageObject pageObject = pageObjectFactory.create(UserProfilePageObject.class);

        // then
        assertThat(pageObject.getUsername(), equalTo(username));
    }

    @Test
    public void focus_focusOnPageObject() {
        // given
        WebDriver webDriver = new AbstractWebDriver() {
            private String windowHandle = "someWindow";

            @Override
            public TargetLocator switchTo() {
                return new AbstractTargetLocator() {
                    @Override
                    public WebDriver window(String nameOrHandle) {
                        windowHandle = nameOrHandle;
                        return null;
                    }
                };
            }

            @Override
            public String getWindowHandle() {
                return windowHandle;
            }
        };
        WebDriverBasedPageObjectFactory pageObjectFactory = new WebDriverBasedPageObjectFactory(() -> webDriver);
        UserProfilePageObject pageObject = pageObjectFactory.create(UserProfilePageObject.class);
        String pageObjectWindow = webDriver.getWindowHandle();
        webDriver.switchTo()
                .window("otherWindow");

        // when
        pageObjectFactory.focus(pageObject);

        // then
        assertThat(webDriver.getWindowHandle(), equalTo(pageObjectWindow));
    }
}