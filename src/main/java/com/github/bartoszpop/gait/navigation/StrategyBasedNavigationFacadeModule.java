package com.github.bartoszpop.gait.navigation;

import static com.google.inject.util.Types.newParameterizedType;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;

/**
 * This Guice module binds the {@link StrategyBasedNavigationFacade}{@code <T>} parametrized type to the {@link NavigationFacade}, where {@code T} is a type
 * argument of this class. A subclass of this class should bind the concrete {@link StrategyBasedNavigationFacade} implementation and all the necessary
 * dependencies.
 *
 * @param <T> the type of the page object corresponding to the reference component the facade being bound navigates through
 * @author Bartosz Popiela
 */
public abstract class StrategyBasedNavigationFacadeModule<T> extends AbstractModule {

    // XXX cast allowed because the class object corresponds to the argument type T
    @SuppressWarnings("unchecked")
    private final Class<T> referenceComponentClass = (Class<T>) new TypeToken<T>(getClass()) {
    }.getRawType();

    // XXX cast allowed because the TypeLiteral object corresponds to the StrategyBasedNavigationFacade<T>
    @SuppressWarnings("unchecked")
    private final TypeLiteral<StrategyBasedNavigationFacade<T>> navigationFacadeType = (TypeLiteral<StrategyBasedNavigationFacade<T>>) TypeLiteral.get(
            newParameterizedType(StrategyBasedNavigationFacade.class, referenceComponentClass));

    @Override
    protected final void configure() {
        bind(NavigationFacade.class).to(navigationFacadeType);
        createBindings();
    }

    /**
     * Creates bindings to the concrete {@link StrategyBasedNavigationFacade}{@code <T>} implementation and all the necessary dependencies.
     */
    @SuppressWarnings("WeakerAccess")
    protected abstract void createBindings();

    @SuppressWarnings("unused")
    protected final AnnotatedBindingBuilder<StrategyBasedNavigationFacade<T>> bindNavigationFacade() {
        return bind(navigationFacadeType);
    }
}