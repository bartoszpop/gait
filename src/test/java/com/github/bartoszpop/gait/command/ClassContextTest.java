package com.github.bartoszpop.gait.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
public final class ClassContextTest {
    @Test
    public void toOuterContext_null() {
        // given
        ClassContext classContext = new ClassContext(getClass());

        // when
        FixtureContext outerContext = classContext.toOuterContext();

        // then
        assertThat(outerContext, nullValue());
    }
}