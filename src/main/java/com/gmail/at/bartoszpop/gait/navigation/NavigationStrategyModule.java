package com.gmail.at.bartoszpop.gait.navigation;

import static com.google.inject.util.Types.newParameterizedType;
import static com.google.inject.util.Types.subtypeOf;
import static com.google.inject.util.Types.supertypeOf;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;

/**
 * This Guice module creates a type-safe binder for strategies navigating from the page object of type {@code T}.
 *
 * @param <T> the type of the page object corresponding to the reference component strategies being bound must navigate from
 * @author Bartosz Popiela
 */
public abstract class NavigationStrategyModule<T> extends AbstractModule {

    // XXX cast allowed because the class object corresponds to the argument type T
    @SuppressWarnings("unchecked")
    private final Class<T> referenceComponentClass = (Class<T>) new TypeToken<T>(getClass()) {
    }.getRawType();

    // XXX cast allowed because the TypeLiteral object corresponds to the NavigationStrategy<? super T, ?>
    @SuppressWarnings("unchecked")
    private final TypeLiteral<NavigationStrategy<? super T, ?>> navigationStrategyType = (TypeLiteral<NavigationStrategy<? super T, ?>>) TypeLiteral.get(
            newParameterizedType(NavigationStrategy.class, supertypeOf(referenceComponentClass), subtypeOf(Object.class)));

    @Override
    protected final void configure() {
        createBindings();
    }

    /**
     * Creates bindings to strategy classes and all the necessary dependencies.
     */
    protected abstract void createBindings();

    @SuppressWarnings("WeakerAccess")
    protected final LinkedBindingBuilder<NavigationStrategy<? super T, ?>> bindNavigationStrategy() {
        return Multibinder.newSetBinder(binder(), navigationStrategyType)
                .addBinding();
    }
}