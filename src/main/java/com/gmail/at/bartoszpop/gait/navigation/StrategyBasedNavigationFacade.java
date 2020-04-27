package com.gmail.at.bartoszpop.gait.navigation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import net.jcip.annotations.NotThreadSafe;

/**
 * This facade navigates through the UI with use of the page object of type {@code T}. The concept behind is to encapsulate the navigation path in terms of the
 * Strategy design pattern.
 *
 * <p>In most applications, there exists a constantly visible navigation component (eg. a menu bar) that serves as a reference point to navigate through the
 * UI. The concrete implementation should define such a component and the corresponding page object as the type argument {@code T}, and all the necessary steps
 * to get to it from any UI part. To navigate from the reference component a strategy of type {@link NavigationStrategy}{@code <? super T, S>} should be present
 * in the Guice injection context, where {@code S} is a type of the page object corresponding to the destination component.
 *
 * <p>To bind a concrete implementation of this facade in the Guice injection context the {@link StrategyBasedNavigationFacadeModule} may be extended.
 * Moreover, to bind {@link NavigationStrategy} implementations the {@link NavigationStrategyModule} may be extended.
 *
 * @param <T> the type of the page object corresponding to the reference component
 * @author Bartosz Popiela
 * @see StrategyBasedNavigationFacadeModule
 * @see NavigationStrategyModule
 */
@NotThreadSafe
public abstract class StrategyBasedNavigationFacade<T> implements NavigationFacade {

    private static final int DESTINATION_COMPONENT_TYPE_PARAMETER_INDEX = 1;

    private Multimap<Class<?>, NavigationStrategy<? super T, ?>> destinationComponentClassToStrategy = ImmutableListMultimap.of();

    private Map<Class<? extends NavigationStrategy<? super T, ?>>, NavigationStrategy<? super T, ?>> strategyClassToStrategy = ImmutableMap.of();

    protected StrategyBasedNavigationFacade() {
    }

    /**
     * {@inheritDoc}
     *
     * @implSpec If more than one instance of type {@link NavigationStrategy}{@code <? super T, S>} is present in the Guice injection context, it is unspecified
     * which one will be used to navigate to the target page object.
     */
    @Override
    public final <S> S navigateTo(Class<S> destinationType) {
        // XXX cast allowed because the type of the strategy class had been checked before the mapping was created.
        // noinspection unchecked
        Collection<NavigationStrategy<? super T, S>> strategies = (Collection<NavigationStrategy<? super T, S>>) (Collection<? extends NavigationStrategy<? super T, ?>>) destinationComponentClassToStrategy.get(
                destinationType);
        // noinspection PointlessBooleanExpression
        checkArgument(strategies != null && false == strategies.isEmpty(),
                "No navigation strategy for a page object of type " + destinationType.getName() + " has been found.");
        NavigationStrategy<? super T, S> anyStrategy = strategies.iterator()
                .next();
        return anyStrategy.navigateFrom(navigateToReferenceComponent());
    }

    /**
     * This method navigates to the page object defined by a given strategy class.
     *
     * @param strategyClass the class of the strategy to be used to navigate to the destination page object
     * @param <S> the type of the page object to navigate to
     * @return the destination page object
     * @apiNote The {@link #navigateTo(Class)} method should be preferred because this method creates a coupling to the strategy class.
     */
    @SuppressWarnings("unused")
    public final <S> S navigateBy(Class<? extends NavigationStrategy<? super T, S>> strategyClass) {
        // XXX cast allowed because the type of the strategy class had been checked before the mapping was created.
        // noinspection unchecked
        NavigationStrategy<? super T, S> strategy = (NavigationStrategy<? super T, S>) strategyClassToStrategy.get(strategyClass);
        checkNotNull(strategy, "No navigation strategy of type " + strategyClass + " has been found.");
        return strategy.navigateFrom(navigateToReferenceComponent());
    }

    /**
     * This method navigates to the page object corresponding to the reference component.
     *
     * @return the page object corresponding to the reference component
     * @implSpec This method must not not reference the {@link NavigationFacade} because it may lead to the invocation cycle.
     */
    @SuppressWarnings("WeakerAccess")
    protected abstract T navigateToReferenceComponent();

    @Inject
    private void setNavigationStrategies(Set<NavigationStrategy<? super T, ?>> navigationStrategies) {
        ImmutableListMultimap.Builder<Class<?>, NavigationStrategy<? super T, ?>> destinationComponentClassToStrategyBuilder = ImmutableListMultimap.builder();
        ImmutableMap.Builder<Class<? extends NavigationStrategy<? super T, ?>>, NavigationStrategy<? super T, ?>> strategyClassToStrategyBuilder = ImmutableMap.builder();
        for (NavigationStrategy<? super T, ?> strategy : navigationStrategies) {
            Class<?> destinationComponentClass = TypeToken.of(strategy.getClass())
                    .resolveType(NavigationStrategy.class.getTypeParameters()[DESTINATION_COMPONENT_TYPE_PARAMETER_INDEX])
                    .getRawType();
            destinationComponentClassToStrategyBuilder.put(destinationComponentClass, strategy);
            // XXX cast allowed because the type of the strategy class has been checked as a type of dependency to inject.
            // noinspection unchecked
            strategyClassToStrategyBuilder.put((Class<? extends NavigationStrategy<? super T, ?>>) strategy.getClass(), strategy);
        }
        this.destinationComponentClassToStrategy = destinationComponentClassToStrategyBuilder.build();
        this.strategyClassToStrategy = strategyClassToStrategyBuilder.build();
    }
}