package com.github.bartoszpop.gait.command;

import static java.lang.System.identityHashCode;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.annotations.Factory;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

/**
 * @author Bartosz Popiela
 */
public final class CommandListenerTest {

    @Test
    public void beforeClassAnnotatedClass_commandClassScoped() {
        // given
        AtomicReference<OrderedCommand> someClassSomeInstanceSomeMethodCommand = new AtomicReference<>();
        AtomicReference<OrderedCommand> someClassSomeInstanceOtherMethodCommand = new AtomicReference<>();
        AtomicReference<OrderedCommand> someClassOtherInstanceCommand = new AtomicReference<>();
        AtomicReference<OrderedCommand> otherClassCommand = new AtomicReference<>();

        @BeforeClass({ FooCommand.class })
        class QuxTestClass {

            private final AtomicReference<OrderedCommand> someMethodCommand;

            private final AtomicReference<OrderedCommand> otherMethodCommand;

            public QuxTestClass(AtomicReference<OrderedCommand> someMethodCommand, AtomicReference<OrderedCommand> otherMethodCommand) {
                this.someMethodCommand = someMethodCommand;
                this.otherMethodCommand = otherMethodCommand;
            }

            @Factory
            Object[] testInstances() {
                return new Object[] { //
                        new QuxTestClass(someClassSomeInstanceSomeMethodCommand, someClassSomeInstanceOtherMethodCommand), //
                        new QuxTestClass(someClassOtherInstanceCommand, new AtomicReference<>()) };
            }

            @Test
            public void corgeTestMethod() {
                someMethodCommand.set(StaticCommand.unique(FooCommand.class).namedAs(Annotations.beforeClassAnnotationName() + "{" + new InstanceMethod(this) {
                } + "}"));
            }

            @Test
            public void graultTestMethod() {
                otherMethodCommand.set(StaticCommand.unique(FooCommand.class).namedAs(Annotations.beforeClassAnnotationName() + "{" + new InstanceMethod(this) {
                } + "}"));
            }
        }

        @BeforeClass({ FooCommand.class })
        class QuuxTestClass {
            @Test
            public void garplyTestMethod() {
                otherClassCommand.set(StaticCommand.last(FooCommand.class).namedAs(Annotations.beforeClassAnnotationName() + "{" + new InstanceMethod(this) {
                } + "{"));
            }
        }

        // when
        TestNG.asSuite(asList(new QuxTestClass(new AtomicReference<>(), new AtomicReference<>()), new QuuxTestClass()));

        // then
        assertThat(someClassSomeInstanceSomeMethodCommand.get(), equalTo(someClassSomeInstanceOtherMethodCommand.get()));
        assertThat(someClassSomeInstanceSomeMethodCommand.get(), equalTo(someClassOtherInstanceCommand.get()));
        assertThat(someClassSomeInstanceSomeMethodCommand.get(), not(equalTo(otherClassCommand.get())));
    }

    @Test
    public void beforeClassAnnotatedClass_commandsRedoneInOrder() {
        // given
        @BeforeClass({ FooCommand.class, BarCommand.class })
        class TestClass {
            @Test
            public void testMethod() {
            }
        }

        // when
        TestNG.asSuite(new TestClass());

        // then
        OrderedCommand firstBeforeClassCommand = StaticCommand.unique(FooCommand.class).namedAs(Annotations.beforeClassAnnotationName() + "[0]");
        OrderedCommand secondBeforeClassCommand = StaticCommand.unique(BarCommand.class).namedAs(Annotations.beforeClassAnnotationName() + "[1]");
        List<OrderedCommand> sortedByIndex = Stream.of(firstBeforeClassCommand, secondBeforeClassCommand)
                .sorted(OrderedCommand.byIndex())
                .collect(toList());
        assertThat(sortedByIndex, contains(firstBeforeClassCommand, secondBeforeClassCommand));
    }

    @Test
    public void beforeClassAnnotatedClass_implementsFixturable_commandsRedoneInOrder() {
        // given
        OrderedCommand beforeClassFixtureCommand = new OrderedCommand(Fixturables.beforeClassFixturableMethodName());
        OrderedCommand beforeInstanceFixtureCommand = new OrderedCommand(Fixturables.beforeInstanceFixturableMethodName());
        OrderedCommand beforeMethodFixtureCommand = new OrderedCommand(Fixturables.beforeMethodFixturableMethodName());
        OrderedCommand testMethodCommand = new OrderedCommand("testMethod");

        @BeforeClass(FooCommand.class)
        @BeforeInstance(BarCommand.class)
        @BeforeMethod(BazCommand.class)
        @Guice
        class TestClass implements Fixturable {
            @Inject
            private CommandFacade commandFacade;

            @Override
            public List<Fixture<?>> beforeClass() {
                return singletonList(new Fixture<>(() -> beforeClassFixtureCommand));
            }

            @Override
            public List<Fixture<?>> beforeInstance() {
                return singletonList(new Fixture<>(() -> beforeInstanceFixtureCommand));
            }

            @Override
            public List<Fixture<?>> beforeMethod() {
                return singletonList(new Fixture<>(() -> beforeMethodFixtureCommand));
            }

            @Test
            public void testMethod() {
                commandFacade.redo(testMethodCommand);
            }
        }

        // when
        TestNG.asSuite(new TestClass());

        // then
        OrderedCommand beforeClassAnnotationCommand = StaticCommand.unique(FooCommand.class).namedAs(Annotations.beforeClassAnnotationName());
        OrderedCommand beforeInstanceAnnotationCommand = StaticCommand.unique(BarCommand.class).namedAs(Annotations.beforeInstanceAnnotationName());
        OrderedCommand beforeMethodAnnotationCommand = StaticCommand.unique(BazCommand.class).namedAs(Annotations.beforeMethodAnnotationName());
        List<OrderedCommand> sortedByIndex = Stream.of(beforeClassAnnotationCommand, beforeInstanceAnnotationCommand, beforeMethodAnnotationCommand,
                beforeClassFixtureCommand, beforeInstanceFixtureCommand, beforeMethodFixtureCommand, testMethodCommand)
                .sorted(OrderedCommand.byIndex())
                .collect(toList());
        assertThat(sortedByIndex,
                contains(beforeClassAnnotationCommand, beforeClassFixtureCommand, beforeInstanceAnnotationCommand, beforeInstanceFixtureCommand,
                        beforeMethodAnnotationCommand, beforeMethodFixtureCommand, testMethodCommand));
    }

    @Test
    public void beforeInstanceAnnotatedClass_commandInstanceScoped() {
        // given
        AtomicReference<OrderedCommand> someInstanceCommand = new AtomicReference<>();
        AtomicReference<OrderedCommand> otherInstanceCommand = new AtomicReference<>();

        @BeforeInstance({ FooCommand.class })
        class TestClass {

            private final AtomicReference<OrderedCommand> command;

            public TestClass(AtomicReference<OrderedCommand> command) {
                this.command = command;
            }

            @Factory
            Object[] testInstances() {
                return new Object[] { //
                        new TestClass(someInstanceCommand), new TestClass(otherInstanceCommand) };
            }

            @Test
            public void testMethod() {
                command.set(StaticCommand.last(FooCommand.class).namedAs(Annotations.beforeInstanceAnnotationName() + "{" + new InstanceMethod(this) {
                } + "}"));
            }
        }

        // when
        TestNG.asSuite(new TestClass(new AtomicReference<>()));

        // then
        assertThat(someInstanceCommand.get(), not(equalTo(otherInstanceCommand.get())));
    }

    @Test
    public void beforeInstanceAnnotatedClass_commandsRedoneInOrder() {
        // given
        @BeforeInstance({ FooCommand.class, BarCommand.class })
        class TestClass {
            @Test
            public void testMethod() {
            }
        }

        // when
        TestNG.asSuite(new TestClass());

        // then
        OrderedCommand firstBeforeInstanceCommand = StaticCommand.unique(FooCommand.class).namedAs(Annotations.beforeInstanceAnnotationName() + "[0]");
        OrderedCommand secondBeforeInstanceCommand = StaticCommand.unique(BarCommand.class).namedAs(Annotations.beforeInstanceAnnotationName() + "[1]");
        List<OrderedCommand> sortedByIndex = Stream.of(firstBeforeInstanceCommand, secondBeforeInstanceCommand)
                .sorted(OrderedCommand.byIndex())
                .collect(toList());
        assertThat(sortedByIndex, contains(firstBeforeInstanceCommand, secondBeforeInstanceCommand));
    }

    @Test
    public void beforeMethodAnnotatedClass_commandMethodScoped() {
        // given
        Queue<OrderedCommand> someInstanceSomeMethodCommands = new LinkedList<>();
        AtomicReference<OrderedCommand> someInstanceOtherMethodCommand = new AtomicReference<>();
        AtomicReference<OrderedCommand> otherInstanceOtherMethodCommand = new AtomicReference<>();

        @BeforeMethod({ FooCommand.class })
        class QuxTestClass {

            private final Queue<OrderedCommand> someMethodCommands;

            private final AtomicReference<OrderedCommand> otherMethodCommand;

            public QuxTestClass(Queue<OrderedCommand> someMethodCommands, AtomicReference<OrderedCommand> otherMethodCommand) {
                this.someMethodCommands = someMethodCommands;
                this.otherMethodCommand = otherMethodCommand;
            }

            @Factory
            Object[] testInstances() {
                return new Object[] { //
                        new QuxTestClass(someInstanceSomeMethodCommands, someInstanceOtherMethodCommand), //
                        new QuxTestClass(new LinkedList<>(), otherInstanceOtherMethodCommand) };
            }

            @Test(invocationCount = 2)
            public void corgeTestMethod() {
                someMethodCommands.add(StaticCommand.last(FooCommand.class).namedAs(Annotations.beforeMethodAnnotationName() + "{" + new InstanceMethod(this) {
                } + "}"));
            }

            @Test
            public void graultTestMethod() {
                otherMethodCommand.set(StaticCommand.last(FooCommand.class).namedAs(Annotations.beforeMethodAnnotationName() + "{" + new InstanceMethod(this) {
                } + "}"));
            }
        }

        // when
        TestNG.asSuite(new QuxTestClass(new LinkedList<>(), new AtomicReference<>()));

        // then
        assertThat(someInstanceSomeMethodCommands.peek(), not(equalTo(someInstanceOtherMethodCommand.get())));
        assertThat(someInstanceOtherMethodCommand.get(), not(equalTo(otherInstanceOtherMethodCommand.get())));
        assertThat(someInstanceSomeMethodCommands.poll(), not(equalTo(someInstanceSomeMethodCommands.peek())));
    }

    @Test
    public void beforeMethodAnnotatedClass_commandsRedoneInOrder() {
        // given
        @BeforeMethod({ FooCommand.class, BarCommand.class })
        class TestClass {
            @Test
            public void testMethod() {
            }
        }

        // when
        TestNG.asSuite(new TestClass());

        // then
        OrderedCommand firstBeforeMethodCommand = StaticCommand.unique(FooCommand.class).namedAs(Annotations.beforeMethodAnnotationName() + "[0]");
        OrderedCommand secondBeforeMethodCommand = StaticCommand.unique(BarCommand.class).namedAs(Annotations.beforeMethodAnnotationName() + "[1]");
        List<OrderedCommand> sortedByIndex = Stream.of(firstBeforeMethodCommand, secondBeforeMethodCommand)
                .sorted(OrderedCommand.byIndex())
                .collect(toList());
        assertThat(sortedByIndex, contains(firstBeforeMethodCommand, secondBeforeMethodCommand));
    }

    @Test
    public void beforeMethodAnnotatedMethod_commandMethodScoped() {
        // given
        Queue<OrderedCommand> someInstanceCommands = new LinkedList<>();
        Queue<OrderedCommand> otherInstanceCommands = new LinkedList<>();

        class TestClass {

            private final Queue<OrderedCommand> commands;

            public TestClass(Queue<OrderedCommand> commands) {
                this.commands = commands;
            }

            @Factory
            Object[] testInstances() {
                return new Object[] { //
                        new TestClass(someInstanceCommands), //
                        new TestClass(otherInstanceCommands) };
            }

            @BeforeMethod({ FooCommand.class })
            @Test(invocationCount = 2)
            public void testMethod() {
                commands.add(StaticCommand.last(FooCommand.class).namedAs(Annotations.beforeMethodAnnotationName() + "{" + new InstanceMethod(this) {
                } + "}"));
            }
        }

        // when
        TestNG.asSuite(new TestClass(new LinkedList<>()));

        // then
        assertThat(someInstanceCommands.peek(), not(equalTo(otherInstanceCommands.peek())));
        assertThat(someInstanceCommands.poll(), not(equalTo(someInstanceCommands.peek())));
    }

    @Test
    public void beforeMethodAnnotatedMethod_commandsRedoneInOrder() {
        // given
        class TestClass {
            @BeforeMethod({ FooCommand.class, BarCommand.class })
            @Test
            public void testMethod() {
            }
        }

        // when
        TestNG.asSuite(new TestClass());

        // then
        OrderedCommand firstBeforeMethodCommand = StaticCommand.unique(FooCommand.class).namedAs(Annotations.beforeMethodAnnotationName() + "[0]");
        OrderedCommand secondBeforeMethodCommand = StaticCommand.unique(BarCommand.class).namedAs(Annotations.beforeMethodAnnotationName() + "[1]");
        List<OrderedCommand> sortedByIndex = Stream.of(firstBeforeMethodCommand, secondBeforeMethodCommand)
                .sorted(OrderedCommand.byIndex())
                .collect(toList());
        assertThat(sortedByIndex, contains(firstBeforeMethodCommand, secondBeforeMethodCommand));
    }

    @Test
    public void implementsFixturableBeforeClass_commandClassScoped() {
        // given
        AtomicReference<OrderedCommand> someClassSomeInstanceSomeMethodCommand = new AtomicReference<>();
        AtomicReference<OrderedCommand> someClassSomeInstanceOtherMethodCommand = new AtomicReference<>();
        AtomicReference<OrderedCommand> someClassOtherInstanceCommand = new AtomicReference<>();
        AtomicReference<OrderedCommand> otherClassCommand = new AtomicReference<>();

        class QuxTestClass implements Fixturable {

            private final AtomicReference<OrderedCommand> someMethodCommand;

            private final AtomicReference<OrderedCommand> otherMethodCommand;

            private Fixture<OrderedCommand> fixture;

            public QuxTestClass(AtomicReference<OrderedCommand> someMethodCommand, AtomicReference<OrderedCommand> otherMethodCommand) {
                this.someMethodCommand = someMethodCommand;
                this.otherMethodCommand = otherMethodCommand;
            }

            @Factory
            Object[] testInstances() {
                return new Object[] { //
                        new QuxTestClass(someClassSomeInstanceSomeMethodCommand, someClassSomeInstanceOtherMethodCommand), //
                        new QuxTestClass(someClassOtherInstanceCommand, new AtomicReference<>()) };
            }

            @Override
            public List<Fixture<?>> beforeClass() {
                return singletonList(fixture = new Fixture<>(() -> new OrderedCommand(Fixturables.beforeClassFixturableMethodName())));
            }

            @Test
            public void corgeTestMethod() {
                someMethodCommand.set(fixture.get()
                        .namedAs(Fixturables.beforeClassFixturableMethodName() + "{" + new InstanceMethod(this) {
                        } + "}"));
            }

            @Test
            public void graultTestMethod() {
                otherMethodCommand.set(fixture.get()
                        .namedAs(Fixturables.beforeClassFixturableMethodName() + "{" + new InstanceMethod(this) {
                        } + "}"));
            }
        }

        class QuuxTestClass implements Fixturable {

            private Fixture<OrderedCommand> fixture;

            @Override
            public List<Fixture<?>> beforeClass() {
                return singletonList(fixture = new Fixture<>(() -> new OrderedCommand(Fixturables.beforeClassFixturableMethodName())));
            }

            @Test
            public void garplyTestMethod() {
                otherClassCommand.set(fixture.get()
                        .namedAs(Fixturables.beforeClassFixturableMethodName() + "{" + new InstanceMethod(this) {
                        } + "{"));
            }
        }

        // when
        TestNG.asSuite(asList(new QuxTestClass(new AtomicReference<>(), new AtomicReference<>()), new QuuxTestClass()));

        // then
        assertThat(someClassSomeInstanceSomeMethodCommand.get(), equalTo(someClassSomeInstanceOtherMethodCommand.get()));
        assertThat(someClassSomeInstanceSomeMethodCommand.get(), equalTo(someClassOtherInstanceCommand.get()));
        assertThat(someClassSomeInstanceSomeMethodCommand.get(), not(equalTo(otherClassCommand.get())));
    }

    @Test
    public void implementsFixturableBeforeClass_commandsRedoneInOrder() {
        // given
        OrderedCommand firstBeforeClassCommand = new OrderedCommand(Fixturables.beforeClassFixturableMethodName() + "[0]");
        OrderedCommand secondBeforeClassCommand = new OrderedCommand(Fixturables.beforeClassFixturableMethodName() + "[1]");

        class TestClass implements Fixturable {
            @Override
            public List<Fixture<?>> beforeClass() {
                return asList(new Fixture<>(() -> firstBeforeClassCommand), new Fixture<>(() -> secondBeforeClassCommand));
            }

            @Test
            public void testMethod() {
            }
        }

        // when
        TestNG.asSuite(new TestClass());

        // then
        List<OrderedCommand> sortedByIndex = Stream.of(firstBeforeClassCommand, secondBeforeClassCommand)
                .sorted(OrderedCommand.byIndex())
                .collect(toList());
        assertThat(sortedByIndex, contains(firstBeforeClassCommand, secondBeforeClassCommand));
    }

    @Test
    public void implementsFixturableBeforeInstance_commandInstanceScoped() {
        // given
        AtomicReference<OrderedCommand> someInstanceSomeMethodCommand = new AtomicReference<>();
        AtomicReference<OrderedCommand> someInstanceOtherMethodCommand = new AtomicReference<>();
        AtomicReference<OrderedCommand> otherInstanceCommand = new AtomicReference<>();

        class QuxTestClass implements Fixturable {

            private final AtomicReference<OrderedCommand> someMethodCommand;

            private final AtomicReference<OrderedCommand> otherMethodCommand;

            private Fixture<OrderedCommand> fixture;

            public QuxTestClass(AtomicReference<OrderedCommand> someMethodCommand, AtomicReference<OrderedCommand> otherMethodCommand) {
                this.someMethodCommand = someMethodCommand;
                this.otherMethodCommand = otherMethodCommand;
            }

            @Factory
            Object[] testInstances() {
                return new Object[] { //
                        new QuxTestClass(someInstanceSomeMethodCommand, someInstanceOtherMethodCommand), //
                        new QuxTestClass(otherInstanceCommand, new AtomicReference<>()) };
            }

            @Override
            public List<Fixture<?>> beforeInstance() {
                return singletonList(fixture = new Fixture<>(() -> new OrderedCommand(Fixturables.beforeInstanceFixturableMethodName())));
            }

            @Test
            public void corgeTestMethod() {
                someMethodCommand.set(fixture.get()
                        .namedAs(Fixturables.beforeInstanceFixturableMethodName() + "{" + new InstanceMethod(this) {
                        } + "}"));
            }

            @Test
            public void graultTestMethod() {
                otherMethodCommand.set(fixture.get()
                        .namedAs(Fixturables.beforeInstanceFixturableMethodName() + "{" + new InstanceMethod(this) {
                        } + "}"));
            }
        }

        // when
        TestNG.asSuite(new QuxTestClass(new AtomicReference<>(), new AtomicReference<>()));

        // then
        assertThat(someInstanceSomeMethodCommand.get(), equalTo(someInstanceOtherMethodCommand.get()));
        assertThat(someInstanceSomeMethodCommand.get(), not(equalTo(otherInstanceCommand.get())));
    }

    @Test
    public void implementsFixturableBeforeInstance_commandsRedoneInOrder() {
        // given
        OrderedCommand firstBeforeInstanceCommand = new OrderedCommand(Fixturables.beforeInstanceFixturableMethodName() + "[0]");
        OrderedCommand secondBeforeInstanceCommand = new OrderedCommand(Fixturables.beforeInstanceFixturableMethodName() + "[1]");

        class TestClass implements Fixturable {
            @Override
            public List<Fixture<?>> beforeInstance() {
                return asList(new Fixture<>(() -> firstBeforeInstanceCommand), new Fixture<>(() -> secondBeforeInstanceCommand));
            }

            @Test
            public void testMethod() {
            }
        }

        // when
        TestNG.asSuite(new TestClass());

        // then
        List<OrderedCommand> sortedByIndex = Stream.of(firstBeforeInstanceCommand, secondBeforeInstanceCommand)
                .sorted(OrderedCommand.byIndex())
                .collect(toList());
        assertThat(sortedByIndex, contains(firstBeforeInstanceCommand, secondBeforeInstanceCommand));
    }

    @Test
    public void implementsFixturableBeforeMethod_commandMethodScoped() {
        // given
        Queue<OrderedCommand> someInstanceSomeMethodCommands = new LinkedList<>();
        AtomicReference<OrderedCommand> someInstanceOtherMethodCommand = new AtomicReference<>();
        AtomicReference<OrderedCommand> otherInstanceOtherMethodCommand = new AtomicReference<>();

        class QuxTestClass implements Fixturable {

            private final Queue<OrderedCommand> someMethodCommands;

            private final AtomicReference<OrderedCommand> otherMethodCommand;

            private Fixture<OrderedCommand> fixture;

            QuxTestClass(Queue<OrderedCommand> someMethodCommands, AtomicReference<OrderedCommand> otherMethodCommand) {
                this.someMethodCommands = someMethodCommands;
                this.otherMethodCommand = otherMethodCommand;
            }

            @Factory
            Object[] testInstances() {
                return new Object[] { //
                        new QuxTestClass(someInstanceSomeMethodCommands, someInstanceOtherMethodCommand), //
                        new QuxTestClass(new LinkedList<>(), otherInstanceOtherMethodCommand) };
            }

            @Override
            public List<Fixture<?>> beforeMethod() {
                return singletonList(fixture = new Fixture<>(() -> new OrderedCommand(Fixturables.beforeClassFixturableMethodName())));
            }

            @Test(invocationCount = 2)
            public void corgeTestMethod() {
                someMethodCommands.add(fixture.get()
                        .namedAs(Fixturables.beforeMethodFixturableMethodName() + "{" + new InstanceMethod(this) {
                        } + "}"));
            }

            @Test
            public void graultTestMethod() {
                otherMethodCommand.set(fixture.get()
                        .namedAs(Fixturables.beforeMethodFixturableMethodName() + "{" + new InstanceMethod(this) {
                        } + "}"));
            }
        }

        // when
        TestNG.asSuite(new QuxTestClass(new LinkedList<>(), new AtomicReference<>()));

        // then
        assertThat(someInstanceSomeMethodCommands.peek(), not(equalTo(someInstanceOtherMethodCommand.get())));
        assertThat(someInstanceOtherMethodCommand.get(), not(equalTo(otherInstanceOtherMethodCommand.get())));
        assertThat(someInstanceSomeMethodCommands.poll(), not(equalTo(someInstanceSomeMethodCommands.peek())));
    }

    @Test
    public void implementsFixturableBeforeMethod_commandsRedoneInOrder() {
        // given
        OrderedCommand firstBeforeMethodCommand = new OrderedCommand(Fixturables.beforeMethodFixturableMethodName() + "[0]");
        OrderedCommand secondBeforeMethodCommand = new OrderedCommand(Fixturables.beforeMethodFixturableMethodName() + "[1]");

        class TestClass implements Fixturable {
            @Override
            public List<Fixture<?>> beforeMethod() {
                return asList(new Fixture<>(() -> firstBeforeMethodCommand), new Fixture<>(() -> secondBeforeMethodCommand));
            }

            @Test
            public void testMethod() {
            }
        }

        // when
        TestNG.asSuite(new TestClass());

        // then
        List<OrderedCommand> sortedByIndex = Stream.of(firstBeforeMethodCommand, secondBeforeMethodCommand)
                .sorted(OrderedCommand.byIndex())
                .collect(toList());
        assertThat(sortedByIndex, contains(firstBeforeMethodCommand, secondBeforeMethodCommand));
    }

    @Test
    public void implementsInjecteeFixturable_guiceAnnotatedClass_commandRedone() {
        // given
        InjecteeCommand injecteeCommand = new InjecteeCommand(null);

        @Guice(modules = { InjectableModule.class })
        class TestClass implements Fixturable {
            @Override
            public List<Fixture<?>> beforeClass() {
                return singletonList(new Fixture<>(() -> injecteeCommand));
            }

            @Test
            public void testMethod() {
            }
        }

        // when
        TestNG.asSuite(new TestClass());

        // then
        MatcherAssert.assertThat(injecteeCommand, CommandMatchers.redone());
    }

    @Test
    public void injecteeBeforeAnnotatedClass_guiceAnnotatedClass_commandRedone() {
        // given
        @Guice(modules = { InjectableModule.class })
        @BeforeClass(InjecteeCommand.class)
        class TestClass {
            @Test
            public void testMethod() {
            }
        }

        // when
        TestNG.asSuite(new TestClass());

        // then
        MatcherAssert.assertThat(InjecteeCommand.injecteeCommand(), CommandMatchers.redone());
    }

    @Test
    public void loginBeforeAnnotatedClass_afterLoginInvoked() {
        // given
        OrderedCommand afterLogin = new OrderedCommand(Annotations.afterLoginAnnotationName());

        @BeforeClass({ NoOpLoginCommand.class })
        @Guice
        class TestClass {
            @Inject
            private CommandFacade commandFacade;

            @AfterLogin
            public void afterLogin() {
                commandFacade.redo(afterLogin);
            }

            @Test
            public void testMethod() {
            }
        }

        // when
        TestNG.asSuite(new TestClass());

        // then
        List<OrderedCommand> sortedByIndex = Stream.of(NoOpLoginCommand.loginCommand(), afterLogin)
                .sorted(OrderedCommand.byIndex())
                .collect(toList());
        MatcherAssert.assertThat(sortedByIndex, Matchers.contains(NoOpLoginCommand.loginCommand(), afterLogin));
    }

    @Test
    public void plainBeforeAnnotatedClass_notGuiceAnnotatedClass_commandRedone() {
        // given
        @BeforeClass(PlainCommand.class)
        class TestClass {
            @Test
            public void testMethod() {
            }
        }

        // when
        TestNG.asSuite(new TestClass());

        // then
        MatcherAssert.assertThat(PlainCommand.plainCommand(), CommandMatchers.redone());
    }

    @Test
    public void redoCommand_commandUndoneAfterSuite() {
        // given
        InjecteeCommand command = new InjecteeCommand(null);

        @Guice(modules = { InjectableModule.class })
        class TestClass {
            @Inject
            private CommandFacade commandFacade;

            @Test
            public void testMethod() {
                commandFacade.redo(command);
            }
        }

        // when
        TestNG.asSuite(new TestClass());

        // then
        MatcherAssert.assertThat(command.undo(), CommandMatchers.redone());
    }

    private static abstract class InstanceMethod {
        private final Object instance;

        public InstanceMethod(Object instance) {
            this.instance = instance;
        }

        public final String toString() {
            return "class=" + instance.getClass()
                    .getSimpleName() + ",instance=" + identityHashCode(instance) + ",method=" + getClass().getEnclosingMethod()
                    .getName() + "()";
        }
    }
}