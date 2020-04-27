package com.github.bartoszpop.gait.command;

import lombok.EqualsAndHashCode;

/**
 * @author Bartosz Popiela
 */
@EqualsAndHashCode(callSuper = false)
final class ClassContext extends FixtureContext {
    private final Class<?> testClass;

    ClassContext(Class<?> testClass) {
        this.testClass = testClass;
    }

    public FixtureContext toOuterContext() {
        return null;
    }
}