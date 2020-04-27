package com.gmail.at.bartoszpop.gait.command;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates a method to be invoked after every {@link LoginCommand} redone on behalf of {@link Fixture} or any {@code @Before}-prefixed annotation.
 *
 * @author Bartosz Popiela
 */
@SuppressWarnings("WeakerAccess")
@Retention(RUNTIME)
@Target(METHOD)
public @interface AfterLogin {
}