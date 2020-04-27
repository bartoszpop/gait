package com.gmail.at.bartoszpop.gait.command;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.gmail.at.bartoszpop.gait.inject.Injector;

import lombok.extern.slf4j.Slf4j;

/**
 * This command is a group of {@link Command Commands}.
 *
 * <p>This {@link Command} injects members to its delegates before redo.
 *
 * @author Bartosz Popiela
 */
@Slf4j
public abstract class CompositeCommand implements Command {

    // This field is intentionally volatile to comply with the double-check idiom and the underlying Deque implementation is thread-safe
    @SuppressWarnings("squid:S3077")
    private volatile Deque<Command> delegates;

    @Inject
    private Injector injector;

    public static CompositeCommand of(Command... delegates) {
        return of(asList(delegates));
    }

    public static CompositeCommand of(List<Command> delegates) {
        return new CompositeCommand() {
            @Override
            protected List<Command> createDelegates() {
                return delegates;
            }
        };
    }

    /**
     * This method redoes delegate commands in the sequential order.
     */
    @Override
    public final void redo() {
        getDelegates().forEach(Command::redo);
    }

    /**
     * Returns {@link CompositeCommand} of delegates undo {@link Command Commands} in the reverse order.
     *
     * @return the undo {@link CompositeCommand}
     */
    @Override
    public Command undo() {
        return CompositeCommand.of(stream(((Iterable<Command>) getDelegates()::descendingIterator).spliterator(), false).map(Command::undo)
                .collect(toList()));
    }

    /**
     * Returns delegate {@link Command Commands} in a way that for each delegate {@link CompositeCommand} it returns its recursively flattened delegate {@link
     * Command Commands}.
     *
     * @return flattened commands this composite command consists of
     */
    public final Deque<Command> flattenDelegates() {
        return getDelegates().stream()
                .flatMap(command -> {
                    if ( command instanceof CompositeCommand ) {
                        return ((CompositeCommand) command).flattenDelegates()
                                .stream();
                    } else {
                        return Stream.of(command);
                    }
                })
                .collect(toCollection(ConcurrentLinkedDeque::new));
    }

    /**
     * Returns delegate {@link Command Commands}.
     *
     * @return commands this composite command consists of
     */
    public final Deque<Command> getDelegates() {
        // double-check idiom
        Deque<Command> result = delegates;
        if ( result == null ) {
            synchronized (this) {
                result = delegates;
                if ( result == null ) {
                    delegates = result = new ConcurrentLinkedDeque<>(createDelegates());
                    if ( injector != null ) {
                        result.forEach(injector::injectTo);
                    }
                }
            }
        }
        return new LinkedList<>(result);
    }

    /**
     * Returns {@link Command Commands} this {@link CompositeCommand} consists of.
     * <br>This method is invoked once when {@link CompositeCommand#getDelegates} is invoked for the first time.
     *
     * @return commands to be redone
     */
    protected abstract List<Command> createDelegates();
}