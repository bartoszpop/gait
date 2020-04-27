package com.github.bartoszpop.gait.inject;

import com.google.inject.AbstractModule;

/**
 * @author Bartosz Popiela
 */
final class InjectableModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Injectable.class).toInstance(new Injectable());
    }

    static class Injectable {
        private Injectable() {
        }
    }
}