package com.gmail.at.bartoszpop.gait.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
public final class InstanceContextTest {
    @Test
    public void toOuterContext_classContext() {
        // given
        InstanceContext instanceContext = new InstanceContext(this);
        ClassContext classContext = new ClassContext(getClass());

        // when
        FixtureContext outerContext = instanceContext.toOuterContext();

        // then
        assertThat(outerContext, equalTo(classContext));
    }
}