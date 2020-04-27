package com.github.bartoszpop.gait.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Bartosz Popiela
 */
final class UserProfilePageObject {
    @FindBy
    private WebElement username;

    public String getUsername() {
        return username.getText();
    }
}