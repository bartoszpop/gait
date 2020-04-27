package com.gmail.at.bartoszpop.gait.inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
public final class SimpleInjectorTest {

    private final SimpleInjector injector = new SimpleInjector();

    @Test
    public void getInstance_instantiatedWithNoArgConstructor() {
        // when
        Object instance = injector.getInstance(Object.class);

        // then
        assertThat(instance, notNullValue());
    }
}