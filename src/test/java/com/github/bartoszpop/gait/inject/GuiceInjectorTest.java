package com.github.bartoszpop.gait.inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import javax.inject.Inject;

import org.testng.annotations.Guice;
import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
@Guice(modules = InjectableModule.class)
public final class GuiceInjectorTest {
    @Inject
    private GuiceInjector injector;

    @Test
    public void getInstance_constructorArgumentsInjected() {
        // when
        Injectee instance = injector.getInstance(Injectee.class);

        // then
        assertThat(instance, notNullValue());
    }

    @Test
    public void injectTo_membersInjected() {
        // given
        Injectee injectee = new Injectee(null);

        // when
        injector.injectTo(injectee);

        // then
        assertThat(injectee.getInjected(), notNullValue());
    }
}
