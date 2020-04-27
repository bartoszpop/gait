package com.github.bartoszpop.gait.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
public final class MethodContextTest {
    @Test
    public void toOuterContext_instanceContext() {
        // given
        MethodContext methodContext = new MethodContext(this, new Object() {
        }.getClass()
                .getEnclosingMethod());
        InstanceContext instanceContext = new InstanceContext(this);

        // when
        FixtureContext outerContext = methodContext.toOuterContext();

        // then
        assertThat(outerContext, equalTo(instanceContext));
    }
}
