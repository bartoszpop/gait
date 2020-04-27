package com.github.bartoszpop.gait.inject;

import javax.inject.Inject;

/**
 * @author Bartosz Popiela
 */
final class GuiceInjector implements Injector {
    private final com.google.inject.Injector delegate;

    @Inject
    public GuiceInjector(com.google.inject.Injector delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return delegate.getInstance(type);
    }

    @Override
    public void injectTo(Object instance) {
        delegate.injectMembers(instance);
    }
}