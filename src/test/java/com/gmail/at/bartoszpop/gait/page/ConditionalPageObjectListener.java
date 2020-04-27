package com.gmail.at.bartoszpop.gait.page;

import java.time.Duration;

import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * @author Bartosz Popiela
 */
public final class ConditionalPageObjectListener implements ISuiteListener {
    @Override
    public void onStart(ISuite suite) {
        System.setProperty(ConditionalPageObjectFactory.DEFAULT_TIMEOUT_PROPERTY, Duration.ofMillis(200)
                .toString());
    }
}