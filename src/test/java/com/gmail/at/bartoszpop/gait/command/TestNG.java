package com.gmail.at.bartoszpop.gait.command;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.IntFunction;

import org.testng.IAlterSuiteListener;
import org.testng.IObjectFactory2;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.xml.XmlSuite;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;

/**
 * @author Bartosz Popiela
 */
final class TestNG {
    private static final int VERBOSITY_LEVEL = 10;

    private static List<Object> testInstances;

    private TestNG() {
    }

    public static List<Object> getTestInstances() {
        if ( testInstances == null ) {
            return emptyList();
        }
        return testInstances;
    }

    public static void asSuite(Object testInstance) {
        asSuite(singletonList(testInstance));
    }

    public static void asSuite(List<Object> testInstances) {
        TestNG.testInstances = testInstances;

        org.testng.TestNG testNg = new org.testng.TestNG(false);
        testNg.setVerbose(VERBOSITY_LEVEL);
        testNg.setTestClasses(testInstances.stream()
                .map(Object::getClass)
                .toArray((IntFunction<Class<?>[]>) Class[]::new));

        // This is a workaround to instantiate a local class with Guice.
        testNg.addListener(new IAlterSuiteListener() {
            @Override
            public void alter(List<XmlSuite> suites) {
                suites.forEach(suite -> suite.setParentModule(LocalClassModule.class.getName()));
            }
        });

        // This is a workaround to inject members to the test instance with a child Guice context.
        testNg.addListener(new ITestListener() {
            @Override
            public void onStart(ITestContext testContext) {
                Map<Class<?>, ITestClass> testClasses = getTestClasses(testContext);
                for (Object testInstance : testInstances) {
                    ITestClass testClass = testClasses.get(testInstance.getClass());
                    Injector injector = testContext.getInjector(testClass);
                    if ( injector != null ) {
                        injector.injectMembers(testInstance);
                    }
                }
            }

            private Map<Class<?>, ITestClass> getTestClasses(ITestContext context) {
                ISuite suite = context.getSuite();
                return suite.getAllMethods()
                        .stream()
                        .map(ITestNGMethod::getTestClass)
                        .distinct()
                        .collect(toMap(ITestClass::getRealClass, identity()));
            }
        });

        testNg.addListener(new ISuiteListener() {
            @Override
            public void onFinish(ISuite suite) {
                TestNG.testInstances = emptyList();
            }
        });

        // This factory is being used in case a test class is not annotated with org.testng.annotations.Guice.
        Map<Class<?>, Queue<Object>> classToInstance = testInstances.stream()
                .collect(groupingBy(Object::getClass, toCollection(LinkedList::new)));
        testNg.setObjectFactory((IObjectFactory2) objectClass -> classToInstance.get(objectClass)
                .remove());

        testNg.run();
    }

    public static final class LocalClassModule extends AbstractModule {
        @Override
        protected void configure() {
            for (Object testInstance : TestNG.testInstances) {
                // Warning suppressed because the instance is being bound to its class
                // noinspection unchecked,rawtypes
                bind((Class) testInstance.getClass()).toInstance(testInstance);
            }
        }
    }
}
