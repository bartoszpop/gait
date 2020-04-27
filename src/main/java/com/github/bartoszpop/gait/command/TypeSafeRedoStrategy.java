package com.github.bartoszpop.gait.command;

import java.lang.reflect.Method;
import java.util.Objects;

import com.google.common.reflect.TypeToken;

/**
 * This strategy applies to {@link Command Commands} for which {@link Command#getClass()} is equal to the type argument {@code T}.
 *
 * @param <T> the type of {@link Command Commands} this {@link RedoStrategy} applies to
 * @author Bartosz Popiela
 */
public abstract class TypeSafeRedoStrategy<T extends Command> implements RedoStrategy {

    private final Class<T> commandClass;

    public TypeSafeRedoStrategy() {
        // XXX cast allowed because the class object corresponds to the argument type T
        //noinspection unchecked
        this.commandClass = (Class<T>) new TypeToken<T>(getClass()) {
        }.getRawType();
    }

    public TypeSafeRedoStrategy(Class<T> commandClass) {
        this.commandClass = commandClass;
    }

    // Warning suppressed because command is of type T
    @SuppressWarnings("unchecked")
    @Override
    public final <S extends Command> SupplierCommand<S> apply(S command, Method testMethod) {
        if ( Objects.equals(commandClass, command.getClass()) //
                && appliesTo((T) command, testMethod) ) {
            return (SupplierCommand<S>) applySafe((T) command, testMethod);
        }
        return null;
    }

    protected boolean appliesTo(T command, Method testMethod) {
        return true;
    }

    protected abstract SupplierCommand<T> applySafe(T command, Method testMethod);
}