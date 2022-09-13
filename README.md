# Guidelines for Automated Integration Testing

The goal of this project is to provide an API to structure automated tests in a clean and composable manner.
It consists of three parts:
- [Page Objects](https://github.com/bartoszpop/gait/tree/master/src/main/java/com/github/bartoszpop/gait/page)
- [Navigation Facade](https://github.com/bartoszpop/gait/tree/master/src/main/java/com/github/bartoszpop/gait/navigation)
- [Commands](https://github.com/bartoszpop/gait/tree/master/src/main/java/com/github/bartoszpop/gait/command)

[Page Object](https://martinfowler.com/bliki/PageObject.html) is a design pattern proposed by Martin Fowler to
encapsulate the details of the UI structure in a dedicated interface. In case the concrete control changes, it should not affect the test code unless the interface changes.

[Navigation Facade](https://github.com/bartoszpop/gait/blob/master/src/main/java/com/github/bartoszpop/gait/navigation/NavigationFacade.java) is an interface to separate the navigation steps from the test steps.
[SpiConfigurableNavigationStrategyModule](https://github.com/bartoszpop/gait/blob/master/src/main/java/com/github/bartoszpop/gait/navigation/SpiConfigurableNavigationStrategyModule.java) is a reference implementation
which loads instances of [NavigationStrategy](https://github.com/bartoszpop/gait/blob/master/src/main/java/com/github/bartoszpop/gait/navigation/NavigationStrategy.java) defined in the [ServiceLoader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) configuration files.

Commands API defines an abstraction for test steps to be reusable, composable, and in case of test failure, revertable. It follows the Command design pattern.
