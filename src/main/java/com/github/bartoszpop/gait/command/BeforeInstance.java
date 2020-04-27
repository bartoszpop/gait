package com.github.bartoszpop.gait.command;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * {@link Command Commands} to be redone once before each instance of the annotated class.
 *
 * @author Bartosz Popiela
 */
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface BeforeInstance {
    Class<? extends Command>[] value() default {};
}
