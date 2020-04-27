package com.gmail.at.bartoszpop.gait.command;

import static com.gmail.at.bartoszpop.gait.command.Command.NO_OP;
import static com.gmail.at.bartoszpop.gait.command.CommandFacade.StrategyLazyHolder.getRedoStrategy;
import static com.gmail.at.bartoszpop.gait.command.CommandFacade.StrategyLazyHolder.getUndoStrategy;
import static com.gmail.at.bartoszpop.gait.command.CommandListener.getCurrentContext;
import static com.gmail.at.bartoszpop.gait.command.RedoStrategy.redoable;

import java.lang.reflect.Method;
import java.util.Deque;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import com.gmail.at.bartoszpop.gait.inject.Injector;
import com.google.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

/**
 * This is a facade to redo {@link Command Commands} with.
 *
 * <p>It may be desirable to skip or redo {@link Command} of the given type only once, eg. to reuse a test fixture. For this purpose, this facade delegates
 * {@link Command Commands} to {@link RedoStrategy} set with the {@value CommandFacade#REDO_STRATEGY_PROPERTY} property or {@link RedoStrategy.Redo} if none
 * set. The strategy results with {@link SupplierCommand} that will have its members injected and be redone on behalf of the expected {@link Command}. {@link
 * SupplierCommand} may or may not redo the {@link Command} underneath. If {@link RedoStrategy} results with null, it defaults to {@link SingleCommand} with the
 * expected {@link Command} as a delegate. {@link #redo(Command)} returns the supplied {@link Command}.
 *
 * <p>Once all tests and its related methods have been called the result {@link Command Commands} are undone in the reverse order. For the undo {@link
 * Command}, this facade delegates to {@link UndoStrategy} set with the {@value CommandFacade#UNDO_STRATEGY_PROPERTY} property or {@link UndoStrategy.Undo} if
 * none set. In case of null, {@link Command#undo()} is called. Undo {@link Command Commands} have its members injected with the same context as the
 * corresponding result {@link Command Commands}.
 *
 * <p>This facade synchronizes on {@link RedoStrategy} and result {@link Command Commands} in a way it is guaranteed that if
 * the {@link RedoStrategy#apply(Command, Method)} call in two different threads results with different suppliers that supply the same {@link Command} instance,
 * then {@link SupplierCommand#redo()} will be called in the atomic manner in the same order as {@link RedoStrategy#apply(Command, Method)} and all changes made
 * by the first thread will be visible to the second thread. In particular, although first and next {@link RedoOnceStrategy#apply(Command, Method)} calls return
 * the same {@link Command} instance but only the first call returns {@link SupplierCommand} that redo this {@link Command} instance, it is guaranteed that the
 * caller will always get {@link Command} that has been redone and will see the up-to-date {@link Command} state. Hence, neither {@link RedoStrategy} nor result
 * {@link Command Commands} need to be synchronized as long as the state doesn't change outside {@link #redo(Command)}.
 *
 * @author Bartosz Popiela
 * @see RedoStrategy
 * @see UndoStrategy
 */
@Slf4j
@Singleton
@ThreadSafe
public final class CommandFacade {

    public static final String REDO_STRATEGY_PROPERTY = "command.redo.strategy";

    public static final String UNDO_STRATEGY_PROPERTY = "command.undo.strategy";

    private static final Deque<Command> undoCommands = new ConcurrentLinkedDeque<>();

    private static final DescendingLock redoLock = new DescendingLock();

    private final Injector injector;

    @Inject
    CommandFacade(Injector injector) {
        this.injector = injector;
    }

    static void undo() {
        for (Iterator<Command> it = undoCommands.descendingIterator(); it.hasNext(); it.remove()) {
            try {
                Command undoCommand = it.next();
                log.debug("Redo an undo command {}.", undoCommand);
                undoCommand.redo();
            } catch (Exception e) {
                log.error("An undo command has failed.", e);
            }
        }
    }

    /**
     * This method redoes {@link SupplierCommand} that {@link RedoStrategy} results with for the given {@link Command} and returns the supplied {@link
     * Command}.
     *
     * @param command {@link Command} to redo
     * @return the supplied {@link Command}
     */
    public <T extends Command> T redo(T command) {
        try {
            log.debug("Redo a command {}.", command);
            SupplierCommand<T> redoCommand = getRedoCommand(command);
            T suppliedCommand = redoAndSupply(redoCommand);
            redoOnUndo(getUndoCommand(redoCommand));
            return suppliedCommand;
        } finally {
            redoLock.unlock();
        }
    }

    private <T extends Command> SupplierCommand<T> getRedoCommand(T command) {
        redoLock.lock(getRedoStrategy());
        Method testMethod = getCurrentContext().getTestMethod();
        SupplierCommand<T> redoCommand = Optional.ofNullable(getRedoStrategy().apply(command, testMethod))
                // intentionally orElse instead orElseGet because Guice can't resolve the type argument in the latter case
                .orElse(redoable(command));
        injector.injectTo(redoCommand);
        log.debug("A redo command is {}.", redoCommand);
        return redoCommand;
    }

    private <T extends Command> T redoAndSupply(SupplierCommand<T> redoCommand) {
        T suppliedCommand = redoCommand.get();
        injector.injectTo(suppliedCommand);
        log.debug("{} was supplied.", suppliedCommand);
        redoLock.lock(suppliedCommand);
        redoCommand.redo();
        log.debug("{} was redone.", redoCommand);
        return suppliedCommand;
    }

    private Command getUndoCommand(Command command) {
        Method testMethod = getCurrentContext().getTestMethod();
        Command undoCommand = Optional.ofNullable(getUndoStrategy().apply(command, testMethod))
                .orElseGet(command::undo);
        injector.injectTo(undoCommand);
        log.debug("An undo command is {}.", undoCommand);
        return undoCommand;
    }

    private void redoOnUndo(Command undoCommand) {
        if ( false == NO_OP.equals(undoCommand) ) {
            undoCommands.add(undoCommand);
        }
    }

    static final class StrategyLazyHolder {
        private static final RedoStrategy redoStrategy = createRedoStrategy();

        private static final UndoStrategy undoStrategy = createUndoStrategy();

        public static RedoStrategy getRedoStrategy() {
            return redoStrategy;
        }

        public static UndoStrategy getUndoStrategy() {
            return undoStrategy;
        }

        private static RedoStrategy createRedoStrategy() {
            String strategyClassName = System.getProperty(REDO_STRATEGY_PROPERTY);
            if ( strategyClassName != null ) {
                try {
                    log.info("Redo strategy {}", strategyClassName);
                    return (RedoStrategy) Class.forName(strategyClassName)
                            .newInstance();
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
                    log.error("Could not instantiate a redo strategy class.", e);
                }
            }
            return new RedoStrategy.Redo();
        }

        private static UndoStrategy createUndoStrategy() {
            String strategyClassName = System.getProperty(UNDO_STRATEGY_PROPERTY);
            if ( strategyClassName != null ) {
                try {
                    log.info("Undo strategy {}", strategyClassName);
                    return (UndoStrategy) Class.forName(strategyClassName)
                            .newInstance();
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
                    log.error("Could not instantiate an undo strategy class.", e);
                }
            }
            return new UndoStrategy.Undo();
        }
    }
}