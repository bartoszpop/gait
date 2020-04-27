package com.gmail.at.bartoszpop.gait.command;

import static com.gmail.at.bartoszpop.gait.command.CommandFacade.undo;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.testng.IClass;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.internal.ConstructorOrMethod;

import com.gmail.at.bartoszpop.gait.inject.Injector;
import com.gmail.at.bartoszpop.gait.inject.SimpleInjector;

public final class CommandListener implements ISuiteListener, ITestListener {

    private static final ThreadLocal<MethodContext> currentContext = new ThreadLocal<>();

    private final Map<Class<?>, List<Fixture<?>>> testClassToBeforeClassFixtures = new ConcurrentHashMap<>();

    private final Set<Object> testInstances = ConcurrentHashMap.newKeySet();

    private final Map<Object, List<Fixture<?>>> testInstanceToBeforeMethodFixtures = new ConcurrentHashMap<>();

    static MethodContext getCurrentContext() {
        return currentContext.get();
    }

    @Override
    public void onTestStart(ITestResult testResult) {
        Object testInstance = testResult.getInstance();
        Method testMethod = getMethod(testResult);
        setCurrentContext(testInstance, testMethod);
        Injector injector = createInjector(testResult);
        setUpBeforeClassFixtures(testInstance, injector);
        setUpBeforeInstanceFixtures(testInstance, injector);
        setUpBeforeMethodFixtures(testInstance, testMethod, injector);
    }

    private Method getMethod(ITestResult testResult) {
        ITestNGMethod testMethod = testResult.getMethod();
        ConstructorOrMethod constructorOrMethod = testMethod.getConstructorOrMethod();
        return constructorOrMethod.getMethod();
    }

    private void setCurrentContext(Object testInstance, Method testMethod) {
        currentContext.set(new MethodContext(testInstance, testMethod));
    }

    private Injector createInjector(ITestResult testResult) {
        IClass iTestClass = testResult.getTestClass();
        ITestContext testContext = testResult.getTestContext();
        com.google.inject.Injector injector = testContext.getInjector(iTestClass);
        if ( injector == null ) {
            return new SimpleInjector();
        } else {
            return injector.getInstance(Injector.class);
        }
    }

    private void setUpBeforeClassFixtures(Object testInstance, Injector injector) {
        Class<?> testClass = testInstance.getClass();
        if ( false == testClassToBeforeClassFixtures.containsKey(testClass) ) {
            synchronized (testClassToBeforeClassFixtures) {
                if ( false == testClassToBeforeClassFixtures.containsKey(testClass) ) {
                    CommandFacade commandFacade = new CommandFacade(injector);
                    instantiate(getBeforeClassCommands(testClass), injector).forEach(command -> redo(commandFacade, testInstance, command));
                    List<Fixture<?>> beforeClassFixtures = getBeforeClassFixtures(testInstance);
                    beforeClassFixtures.forEach(fixture -> fixture.redo(commandFacade, new ClassContext(testClass)));
                    testClassToBeforeClassFixtures.put(testClass, beforeClassFixtures);
                }
            }
        }
        if ( testClassToBeforeClassFixtures.containsKey(testClass) ) {
            delegateBeforeClassFixtures(testInstance);
        }
    }

    private List<Class<? extends Command>> getBeforeClassCommands(Class<?> testClass) {
        BeforeClass beforeClassAnnotation = testClass.getAnnotation(BeforeClass.class);
        if ( beforeClassAnnotation != null ) {
            return asList(beforeClassAnnotation.value());
        } else {
            return emptyList();
        }
    }

    /**
     * In case of more than one instance of the same {@link BeforeClass} annotated class, subsequent instance {@link Fixture#get()} must delegate to the
     * corresponding first instance  {@link Fixture} at the same position.
     */
    private void delegateBeforeClassFixtures(Object testInstance) {
        List<Fixture<?>> classFixtures = getBeforeClassFixtures(testInstance);
        List<Fixture<?>> instanceFixtures = testClassToBeforeClassFixtures.get(testInstance.getClass());
        Iterator<Fixture<?>> classFixtureIterator = classFixtures.iterator();
        Iterator<Fixture<?>> instanceFixtureIterator = instanceFixtures.iterator();
        while (classFixtureIterator.hasNext() && instanceFixtureIterator.hasNext()) {
            // Warning suppressed because the type of a before-class fixture at the given position should be the same for each instance of the same test class
            // noinspection unchecked,rawtypes
            classFixtureIterator.next()
                    .setDelegate((Fixture) instanceFixtureIterator.next());
        }
        if ( classFixtureIterator.hasNext() || instanceFixtureIterator.hasNext() ) {
            throw new IllegalStateException(instanceFixtures.size() + " before-class fixtures expected but got " + classFixtures.size() + ".");
        }
    }

    private List<Fixture<?>> getBeforeClassFixtures(Object testInstance) {
        if ( testInstance instanceof Fixturable ) {
            return ((Fixturable) testInstance).beforeClass();
        } else {
            return emptyList();
        }
    }

    private void setUpBeforeInstanceFixtures(Object testInstance, Injector injector) {
        if ( false == testInstances.contains(testInstance) ) {
            synchronized (testInstances) {
                if ( false == testInstances.contains(testInstance) ) {
                    CommandFacade commandFacade = new CommandFacade(injector);
                    instantiate(getBeforeInstanceCommands(testInstance.getClass()), injector).forEach(command -> redo(commandFacade, testInstance, command));
                    if ( testInstance instanceof Fixturable ) {
                        ((Fixturable) testInstance).beforeInstance()
                                .forEach(fixture -> fixture.redo(commandFacade, new InstanceContext(testInstance)));
                    }
                    testInstances.add(testInstance);
                }
            }
        }
    }

    private List<Class<? extends Command>> getBeforeInstanceCommands(Class<?> testClass) {
        BeforeInstance beforeInstanceAnnotation = testClass.getAnnotation(BeforeInstance.class);
        if ( beforeInstanceAnnotation != null ) {
            return asList(beforeInstanceAnnotation.value());
        } else {
            return emptyList();
        }
    }

    private void setUpBeforeMethodFixtures(Object testInstance, Method testMethod, Injector injector) {
        Class<?> testClass = testInstance.getClass();
        CommandFacade commandFacade = new CommandFacade(injector);
        instantiate(getBeforeMethodCommands(testClass, testMethod), injector).forEach(command -> redo(commandFacade, testInstance, command));
        if ( testInstance instanceof Fixturable ) {
            List<Fixture<?>> beforeMethodFixtures = getBeforeMethodFixtures(testInstance);
            beforeMethodFixtures.forEach(fixture -> fixture.redo(commandFacade, new MethodContext(testInstance, testMethod)));
        }
    }

    private List<Fixture<?>> getBeforeMethodFixtures(Object testInstance) {
        List<Fixture<?>> beforeMethodFixtures = testInstanceToBeforeMethodFixtures.get(testInstance);
        if ( beforeMethodFixtures == null ) {
            // Warning suppressed because testInstance may be referenced by different threads if test methods run in parallel
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (testInstance) {
                beforeMethodFixtures = testInstanceToBeforeMethodFixtures.get(testInstance);
                if ( beforeMethodFixtures == null ) {
                    beforeMethodFixtures = ((Fixturable) testInstance).beforeMethod();
                    testInstanceToBeforeMethodFixtures.put(testInstance, beforeMethodFixtures);
                }
            }
        }
        return beforeMethodFixtures;
    }

    private List<Class<? extends Command>> getBeforeMethodCommands(Class<?> testClass, Method testMethod) {
        BeforeMethod beforeMethodAnnotation = testMethod.getAnnotation(BeforeMethod.class);
        if ( beforeMethodAnnotation == null ) {
            beforeMethodAnnotation = testClass.getAnnotation(BeforeMethod.class);
        }
        if ( beforeMethodAnnotation != null ) {
            return asList(beforeMethodAnnotation.value());
        } else {
            return emptyList();
        }
    }

    private List<Command> instantiate(List<Class<? extends Command>> commandClasses, Injector injector) {
        return commandClasses.stream()
                .map(injector::getInstance)
                .collect(toList());
    }

    private void redo(CommandFacade commandFacade, Object testInstance, Command command) {
        commandFacade.redo(command);
        if ( command instanceof LoginCommand ) {
            invokeAfterLoginMethods(testInstance);
        }
    }

    private void invokeAfterLoginMethods(Object testInstance) {
        getAfterLoginAnnotatedMethods(testInstance.getClass()).forEach(method -> {
            try {
                method.invoke(testInstance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new CommandException("An error occurred while invoking an @" + AfterLogin.class.getSimpleName() + " annotated method.", e);
            }
        });
    }

    private List<Method> getAfterLoginAnnotatedMethods(Class<?> testClass) {
        return Arrays.stream(testClass.getMethods())
                .filter(method -> method.isAnnotationPresent(AfterLogin.class))
                .collect(toList());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        clearCurrentContext();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        clearCurrentContext();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        clearCurrentContext();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        clearCurrentContext();
    }

    private void clearCurrentContext() {
        currentContext.remove();
    }

    @Override
    public void onFinish(ISuite suite) {
        undo();
    }

    @SuppressWarnings("WeakerAccess")
    private static final class CommandException extends RuntimeException {
        public CommandException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}