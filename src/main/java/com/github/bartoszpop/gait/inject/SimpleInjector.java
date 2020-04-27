package com.github.bartoszpop.gait.inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Bartosz Popiela
 */
public final class SimpleInjector implements Injector {
    @Override
    public <T> T getInstance(Class<T> type) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            boolean accessible = constructor.isAccessible();
            synchronized (type) {
                try {
                    constructor.setAccessible(true);
                    return (T) constructor.newInstance();
                } finally {
                    constructor.setAccessible(accessible);
                }
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new ClassNotInstantiatedException(e);
        }
    }

    @Override
    public void injectTo(Object instance) {
        // intentionally left empty
    }

    private static final class ClassNotInstantiatedException extends RuntimeException {
        public ClassNotInstantiatedException(Exception e) {
            super(e);
        }
    }
}