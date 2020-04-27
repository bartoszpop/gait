package com.gmail.at.bartoszpop.gait.command;

import static com.gmail.at.bartoszpop.gait.command.StaticCommand.clearInstances;

import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * @author Bartosz Popiela
 */
public final class StaticCommandListener implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        Object testInstance = result.getInstance();
        Class<?> testClass = testInstance.getClass();
        if ( false == isLocalSuite(testClass) ) {
            clearInstances();
        }
    }

    private boolean isLocalSuite(Class<?> testClass) {
        return TestNG.getTestInstances()
                .stream()
                .map(Object::getClass)
                .anyMatch(testClass::equals);
    }
}