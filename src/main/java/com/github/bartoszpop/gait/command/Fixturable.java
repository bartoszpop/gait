package com.github.bartoszpop.gait.command;

import static java.util.Collections.emptyList;

import java.util.List;

/**
 * A test class that implements this interface may define {@link Fixture Fixtures} to supply a value before a test method.
 * <br>Methods of this interface are invoked once per test instance.
 *
 * @author Bartosz Popiela
 */
// This wildcard type is reasonable because Fixture supplies a value of any type
@SuppressWarnings("squid:S1452")
public interface Fixturable {
    /**
     * Returns ordered {@link Fixture Fixtures} to supply a value once before a first instance of the implementing class.
     *
     * @return {@link Fixture Fixtures} to supply a value
     */
    default List<Fixture<?>> beforeClass() {
        return emptyList();
    }

    /**
     * Returns ordered {@link Fixture Fixtures} to supply a value once before each instance of the implementing class.
     *
     * @return {@link Fixture Fixtures} to supply a value
     */
    default List<Fixture<?>> beforeInstance() {
        return emptyList();
    }

    /**
     * Returns ordered {@link Fixture Fixtures} to supply a value every time before each method of the implementing class.
     *
     * @return {@link Fixture Fixtures} to supply a value
     */
    default List<Fixture<?>> beforeMethod() {
        return emptyList();
    }
}