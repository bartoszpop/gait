package com.github.bartoszpop.gait.command;

import static com.github.bartoszpop.gait.command.OrderedCommand.byIndex;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.github.bartoszpop.gait.inject.Injector;

/**
 * @author Bartosz Popiela
 */
@Guice(modules = { InjectableModule.class })
public final class CompositeCommandTest {

    @Inject
    private Injector injector;

    @Test
    public void redo_delegatesRedoneInOrder() {
        // given
        OrderedCommand firstDelegate = new OrderedCommand("delegates[1]");
        OrderedCommand secondDelegate = new OrderedCommand("delegates[2]");
        CompositeCommand compositeCommand = CompositeCommand.of(firstDelegate, secondDelegate);
        injector.injectTo(compositeCommand);

        // when
        compositeCommand.redo();

        // then
        List<OrderedCommand> sortedByIndex = Stream.of(firstDelegate, secondDelegate)
                .sorted(byIndex())
                .collect(toList());
        assertThat(sortedByIndex, Matchers.contains(firstDelegate, secondDelegate));
    }

    @Test
    public void redo_injecteeDelegateRedone() {
        // given
        InjecteeCommand delegate = new InjecteeCommand(null);
        CompositeCommand compositeCommand = CompositeCommand.of(delegate);
        injector.injectTo(compositeCommand);

        // when
        compositeCommand.redo();

        // then
        MatcherAssert.assertThat(delegate, CommandMatchers.redone());
    }
}