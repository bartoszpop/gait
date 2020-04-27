package com.github.bartoszpop.gait.page;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;

/**
 * @author Bartosz Popiela
 */
public abstract class PageObjectFactoryModule<T extends ConditionalPageObjectFactory & FocusablePageObjectFactory> extends AbstractModule {

    // XXX cast allowed because the class object corresponds to the argument type T
    @SuppressWarnings("unchecked")
    private final Class<T> pageObjectFactoryClass = (Class<T>) new TypeToken<T>(getClass()) {
    }.getRawType();

    @Override
    protected final void configure() {
        bind(PageObjectFactory.class).to(pageObjectFactoryClass);
        bind(ConditionalPageObjectFactory.class).to(pageObjectFactoryClass);
        bind(FocusablePageObjectFactory.class).to(pageObjectFactoryClass);
        createBindings();
    }

    protected abstract void createBindings();

    protected final AnnotatedBindingBuilder<T> bindPageObjectFactory() {
        return bind(pageObjectFactoryClass);
    }
}