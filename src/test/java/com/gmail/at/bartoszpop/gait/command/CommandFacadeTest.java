package com.gmail.at.bartoszpop.gait.command;

import static com.gmail.at.bartoszpop.gait.command.CommandMatchers.redone;
import static com.gmail.at.bartoszpop.gait.command.TestNG.asSuite;
import static java.lang.Thread.currentThread;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
@Guice(modules = { InjectableModule.class })
public final class CommandFacadeTest {

    private final MockRedoStrategy redoStrategy = new MockRedoStrategy();

    private final MockUndoStrategy undoStrategy = new MockUndoStrategy();

    @Inject
    private CommandFacade commandFacade;

    @AfterClass
    public void redoAny() {
        redoStrategy.redoAny();
    }

    @AfterClass
    public void undoAny() {
        undoStrategy.undoAny();
    }

    @Test
    public void redo_commandRedoneInApplyOrder() {
        // given
        final int threadCount = 3;
        CountDownLatch applyLatch = new CountDownLatch(threadCount);
        List<Thread> applyOrder = new ArrayList<>();
        List<Thread> redoOrder = new ArrayList<>();
        redoStrategy.apply((command) -> new SingleCommand<Command>() {
            @Override
            protected Command createDelegate() {
                applyOrder.add(currentThread());
                applyLatch.countDown();
                return command;
            }
        });
        Command command = () -> {
            try {
                applyLatch.await();
                redoOrder.add(currentThread());
            } catch (InterruptedException e) {
                fail("Await failed.", e);
            }
        };

        @Guice(modules = InjectableModule.class)
        class TestClass {
            @Inject
            private CommandFacade commandFacade;

            @Test(invocationCount = threadCount, threadPoolSize = threadCount)
            public void testMethod() {
                commandFacade.redo(command);
            }
        }

        // when
        asSuite(new TestClass());

        // then
        assertThat(redoOrder, equalTo(applyOrder));
    }

    @Test
    public void redo_differentCommandsRedoneInNoOrder() {
        // given
        final int threadCount = 2;
        CountDownLatch redoLatch = new CountDownLatch(threadCount);
        redoStrategy.redoAny();

        @Guice(modules = InjectableModule.class)
        class TestClass {
            @Inject
            private CommandFacade commandFacade;

            @Test(invocationCount = threadCount, threadPoolSize = threadCount)
            public void testMethod() {
                commandFacade.redo(() -> {
                    try {
                        redoLatch.countDown();
                        redoLatch.await();
                    } catch (InterruptedException e) {
                        fail("Await failed.", e);
                    }
                });
            }
        }

        // when
        asSuite(new TestClass());

        // then
        assertThat(redoLatch.getCount(), equalTo(0L));
    }

    @Test
    void redo_redoStrategyApplies_commandSupplierRedone() {
        // given
        redoStrategy.redo(new InjecteeCommand(null));

        // when
        InjecteeCommand resultCommand = commandFacade.redo(new InjecteeCommand(null));

        // then
        assertThat(resultCommand, redone());
    }

    @Test
    void redo_redoStrategyAppliesNot_commandSupplierRedone() {
        // given
        redoStrategy.redoNone();

        // when
        InjecteeCommand resultCommand = commandFacade.redo(new InjecteeCommand(null));

        // then
        assertThat(resultCommand, redone());
    }

    @Test
    void redo_resultsInCommandSupplied() {
        // given
        InjecteeCommand suppliedCommand = new InjecteeCommand(null);
        redoStrategy.redo(suppliedCommand);

        // when
        InjecteeCommand resultCommand = commandFacade.redo(new InjecteeCommand(null));

        // then
        assertThat(resultCommand, equalTo(suppliedCommand));
    }

    @Test
    void redo_undoStrategyApplies_commandUndone() {
        // given
        InjecteeCommand command = new InjecteeCommand(null);
        redoStrategy.redoAny();
        undoStrategy.undoAny();

        // when
        commandFacade.redo(command);
        CommandFacade.undo();

        // then
        assertThat(command.undo(), redone());
    }

    @Test
    void redo_undoStrategyAppliesNot_commandUndone() {
        // given
        InjecteeCommand command = new InjecteeCommand(null);
        redoStrategy.redoAny();
        undoStrategy.undoNone();

        // when
        commandFacade.redo(command);
        CommandFacade.undo();

        // then
        assertThat(command.undo(), redone());
    }
}