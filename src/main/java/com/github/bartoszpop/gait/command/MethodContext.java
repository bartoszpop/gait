package com.github.bartoszpop.gait.command;

import static java.lang.System.identityHashCode;

import java.lang.reflect.Method;
import java.util.Objects;

import lombok.Getter;

/**
 * @author Bartosz Popiela
 */
@Getter
final class MethodContext extends FixtureContext {
    private final Object testInstance;

    private final Method testMethod;

    public MethodContext(Object testInstance, Method testMethod) {
        this.testInstance = testInstance;
        this.testMethod = testMethod;
    }

    public FixtureContext toOuterContext() {
        return new InstanceContext(testInstance);
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        MethodContext that = (MethodContext) o;
        return testInstance == that.testInstance && Objects.equals(testMethod, that.testMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identityHashCode(testInstance), testMethod);
    }
}
