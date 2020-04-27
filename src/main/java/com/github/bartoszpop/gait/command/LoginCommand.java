package com.github.bartoszpop.gait.command;

/**
 * This is a marker interface used to indicate {@link Command} that once redone on behalf of {@link Fixture} or any {@code @Before}-prefixed annotation, {@link
 * AfterLogin @AfterLogin} annotated methods of this test instance should be invoked afterwards.
 *
 * @author Bartosz Popiela
 */
@SuppressWarnings("WeakerAccess")
public interface LoginCommand extends Command {
}