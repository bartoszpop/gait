package com.github.bartoszpop.gait.navigation;

import static com.google.inject.multibindings.Multibinder.newSetBinder;
import static com.google.inject.util.Types.newParameterizedType;
import static com.google.inject.util.Types.subtypeOf;
import static com.google.inject.util.Types.supertypeOf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;

import lombok.extern.slf4j.Slf4j;

/**
 * This Guice module binds strategies defined in Service Provider Interface configuration files for the {@link NavigationStrategy} interface.
 *
 * @author Bartosz Popiela
 * @see <a href="https://docs.oracle.com/javase/tutorial/ext/basics/spi.html">https://docs.oracle.com/javase/tutorial/ext/basics/spi.html</a>
 */
@SuppressWarnings("unused")
@Slf4j
public final class SpiConfigurableNavigationStrategyModule extends AbstractModule {

    private static final int REFERENCE_COMPONENT_TYPE_PARAMETER_INDEX = 0;

    private static final int DESTINATION_COMPONENT_TYPE_PARAMETER_INDEX = 1;

    private static final String PATH_PREFIX = "META-INF/services/";

    @Override
    protected final void configure() {
        bindNavigationStrategies();
    }

    private void bindNavigationStrategies() {
        Map<TypeLiteral<NavigationStrategy<?, ?>>, Multibinder<NavigationStrategy<?, ?>>> typeLiteralToBinder = new HashMap<>();
        for (String strategyClassName : readStrategyClassNames()) {
            try {
                Class<?> strategyClass = Class.forName(strategyClassName);
                if ( NavigationStrategy.class.isAssignableFrom(strategyClass) ) {
                    toBinderTypeLiterals(strategyClass).forEach(binderTypeLiteral -> {
                        Multibinder<NavigationStrategy<?, ?>> binder = typeLiteralToBinder.computeIfAbsent(binderTypeLiteral,
                                typeLiteral -> newSetBinder(binder(), typeLiteral));
                        // warnings suppressed because the strategy class is a subtype of NavigationStrategy<?,?>
                        //noinspection unchecked
                        binder.addBinding()
                                .to((Class<? extends NavigationStrategy<?, ?>>) strategyClass);
                    });
                    log.debug("The strategy class {} has been bound.", strategyClassName);
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("The strategy class " + strategyClassName + " has not been found.", e);
            }
        }
    }

    private List<TypeLiteral<NavigationStrategy<?, ?>>> toBinderTypeLiterals(Class<?> strategyClass) {
        List<TypeLiteral<NavigationStrategy<?, ?>>> binderTypeLiterals = new ArrayList<>();
        TypeToken<?> strategyTypeToken = TypeToken.of(strategyClass);
        Class<?> referenceComponentClass = strategyTypeToken.resolveType(NavigationStrategy.class.getTypeParameters()[REFERENCE_COMPONENT_TYPE_PARAMETER_INDEX])
                .getRawType();
        Class<?> destinationComponentClass = strategyTypeToken.resolveType(
                NavigationStrategy.class.getTypeParameters()[DESTINATION_COMPONENT_TYPE_PARAMETER_INDEX])
                .getRawType();
        Class<?> lowerReferenceComponentBound = referenceComponentClass;
        do {
            Class<?> upperDestinationComponentBound = destinationComponentClass;
            // This binding is necessary in case the destination type is an interface
            // warning suppressed because the type literal corresponds to a subtype of NavigationStrategy
            // noinspection unchecked
            binderTypeLiterals.add((TypeLiteral<NavigationStrategy<?, ?>>) TypeLiteral.get(
                    newParameterizedType(NavigationStrategy.class, supertypeOf(lowerReferenceComponentBound), subtypeOf(Object.class))));
            do {
                // warning suppressed because the type literal corresponds to a subtype of NavigationStrategy
                // noinspection unchecked
                binderTypeLiterals.add((TypeLiteral<NavigationStrategy<?, ?>>) TypeLiteral.get(
                        newParameterizedType(NavigationStrategy.class, lowerReferenceComponentBound, subtypeOf(upperDestinationComponentBound))));
                // warning suppressed because the type literal corresponds to a subtype of NavigationStrategy
                // noinspection unchecked
                binderTypeLiterals.add((TypeLiteral<NavigationStrategy<?, ?>>) TypeLiteral.get(
                        newParameterizedType(NavigationStrategy.class, supertypeOf(lowerReferenceComponentBound), subtypeOf(upperDestinationComponentBound))));
            } while ((upperDestinationComponentBound = upperDestinationComponentBound.getSuperclass()) != null);
        } while ((lowerReferenceComponentBound = lowerReferenceComponentBound.getSuperclass()) != null);
        return binderTypeLiterals;
    }

    private List<String> readStrategyClassNames() {
        List<String> strategyClassNames = new ArrayList<>();
        try {
            Enumeration<URL> resources = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources(PATH_PREFIX + NavigationStrategy.class.getName());
            while (resources.hasMoreElements()) {
                URL configurationFile = resources.nextElement();
                strategyClassNames.addAll(readStrategyClassNamesFromResource(configurationFile));
            }
        } catch (IOException e) {
            throw new ResourceNotReadException("Could not read the configuration files " + PATH_PREFIX + NavigationStrategy.class.getName() + "", e);
        }
        return strategyClassNames;
    }

    private List<String> readStrategyClassNamesFromResource(URL configurationFile) {
        List<String> strategyClassNames = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(configurationFile.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if ( false == line.isEmpty() ) {
                    strategyClassNames.add(line);
                }
            }
        } catch (IOException e) {
            throw new ResourceNotReadException("Could not read the SPI configuration file " + configurationFile, e);
        }
        return strategyClassNames;
    }

    private static class ResourceNotReadException extends RuntimeException {
        @SuppressWarnings("WeakerAccess")
        public ResourceNotReadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}