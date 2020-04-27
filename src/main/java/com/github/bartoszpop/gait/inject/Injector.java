package com.github.bartoszpop.gait.inject;

import com.google.inject.ImplementedBy;

/**
 * @author Bartosz Popiela
 */
@ImplementedBy(GuiceInjector.class)
public interface Injector {
    <T> T getInstance(Class<T> type);

    void injectTo(Object instance);
}