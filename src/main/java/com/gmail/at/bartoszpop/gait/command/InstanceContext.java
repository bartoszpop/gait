package com.gmail.at.bartoszpop.gait.command;

/**
 * @author Bartosz Popiela
 */
final class InstanceContext extends FixtureContext {
    private final Object testInstance;

    public InstanceContext(Object testInstance) {
        this.testInstance = testInstance;
    }

    public FixtureContext toOuterContext() {
        return new ClassContext(testInstance.getClass());
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        InstanceContext that = (InstanceContext) o;
        return testInstance == that.testInstance;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(testInstance);
    }
}