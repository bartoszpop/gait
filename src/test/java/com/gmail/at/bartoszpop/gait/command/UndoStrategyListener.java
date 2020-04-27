package com.gmail.at.bartoszpop.gait.command;

import org.testng.ISuite;
import org.testng.ISuiteListener;

/**
 * @author Bartosz Popiela
 */
public final class UndoStrategyListener implements ISuiteListener {
    @Override
    public void onStart(ISuite suite) {
        System.setProperty(CommandFacade.UNDO_STRATEGY_PROPERTY, MockUndoStrategy.class.getName());
    }
}