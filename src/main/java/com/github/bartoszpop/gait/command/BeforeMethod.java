package com.github.bartoszpop.gait.command;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * If present on a class, {@link Command Commands} to be redone every time before each method of the annotated class.
 * <br>If present on a method, {@link Command Commands} to be redone every time before the annotated method. The method annotation overrides the class
 * annotation for the annotated method.
 *
 * @author Bartosz Popiela
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Inherited
public @interface BeforeMethod {
    Class<? extends Command>[] value() default {};
}
