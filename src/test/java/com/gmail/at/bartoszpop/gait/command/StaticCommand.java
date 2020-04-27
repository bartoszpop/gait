package com.gmail.at.bartoszpop.gait.command;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * @author Bartosz Popiela
 */
abstract class StaticCommand extends CompositeCommand {

    private static final Map<Class<?>, List<OrderedCommand>> classToInstances = new HashMap<>();

    private final OrderedCommand orderedCommand = new OrderedCommand(getClass().getSimpleName());

    {
        classToInstances.computeIfAbsent(getClass(), (commandClass) -> new ArrayList<>())
                .add(orderedCommand);
    }

    static void clearInstances() {
        classToInstances.clear();
    }

    @Nonnull
    protected static OrderedCommand unique(Class<?> commandClass) {
        List<? extends OrderedCommand> commandInstances = classToInstances.get(commandClass);
        if ( commandInstances == null || commandInstances.isEmpty() ) {
            throw new IllegalStateException("No command has been redone.");
        } else if ( commandInstances.size() != 1 ) {
            throw new IllegalStateException("More than one " + commandClass.getSimpleName() + " has been redone.");
        }
        return commandInstances.get(0);
    }

    @Nonnull
    protected static OrderedCommand last(Class<?> commandClass) {
        List<? extends OrderedCommand> commandInstances = classToInstances.get(commandClass);
        if ( commandInstances == null || commandInstances.isEmpty() ) {
            throw new IllegalStateException("No command has been redone.");
        }
        return commandInstances.get(commandInstances.size() - 1);
    }

    @Override
    protected final List<Command> createDelegates() {
        return singletonList(orderedCommand);
    }
}