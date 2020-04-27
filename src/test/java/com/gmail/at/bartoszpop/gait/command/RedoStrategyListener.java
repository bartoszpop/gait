package com.gmail.at.bartoszpop.gait.command;

import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * @author Bartosz Popiela
 */
public final class RedoStrategyListener implements ISuiteListener {
    @Override
    public void onStart(ISuite suite) {
        System.setProperty(CommandFacade.REDO_STRATEGY_PROPERTY, MockRedoStrategy.class.getName());
    }
}