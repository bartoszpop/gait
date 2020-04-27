package com.github.bartoszpop.gait.command;

import static com.google.common.base.Predicates.notNull;
import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.List;

/**
 * This strategy is a group of {@link RedoStrategy RedoStrategies}.
 *
 * @author Bartosz Popiela
 */
public abstract class CompositeRedoStrategy implements RedoStrategy {

    // Warning suppressed because the referenced object is thread-safe
    @SuppressWarnings("squid:S3077")
    private List<RedoStrategy> delegates;

    public static CompositeRedoStrategy of(RedoStrategy... delegates) {
        return of(asList(delegates));
    }

    public static CompositeRedoStrategy of(List<RedoStrategy> delegates) {
        return new CompositeRedoStrategy() {
            @Override
            protected List<RedoStrategy> createDelegates() {
                return delegates;
            }
        };
    }

    @Override
    public final <T extends Command> SupplierCommand<T> apply(T command, Method testMethod) {
        return getDelegates().stream()
                .map(delegate -> delegate.apply(command, testMethod))
                .filter(notNull())
                .findFirst()
                .orElse(null);
    }

    private List<RedoStrategy> getDelegates() {
        if ( delegates == null ) {
            delegates = createDelegates();
        }
        return delegates;
    }

    protected abstract List<RedoStrategy> createDelegates();
}