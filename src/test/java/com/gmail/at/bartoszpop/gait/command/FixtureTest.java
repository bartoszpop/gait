package com.gmail.at.bartoszpop.gait.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.LinkedList;

import org.testng.annotations.Test;

import com.gmail.at.bartoszpop.gait.inject.SimpleInjector;

/**
 * @author Bartosz Popiela
 */
public final class FixtureTest {

    private final CommandFacade commandFacade = new CommandFacade(new SimpleInjector());

    @Test
    public void get_commandClassScoped_commandInstanceScoped_commandMethodScoped_gotMethodScopedCommand() {
        // given
        OrderedCommand classScopedCommand = new OrderedCommand(ClassContext.class.getSimpleName());
        OrderedCommand instanceScopedCommand = new OrderedCommand(InstanceContext.class.getSimpleName());
        OrderedCommand methodScopedCommand = new OrderedCommand(MethodContext.class.getSimpleName());
        Fixture<Command> fixture = new Fixture<>(new LinkedList<OrderedCommand>() {
            {
                add(classScopedCommand);
                add(instanceScopedCommand);
                add(methodScopedCommand);
            }
        }::remove);
        fixture.redo(commandFacade, new ClassContext(getClass()));
        fixture.redo(commandFacade, new InstanceContext(this));
        fixture.redo(commandFacade, new MethodContext(this, new Object() {
        }.getClass()
                .getEnclosingMethod()));

        // when
        Command commandGot = fixture.get();

        // then
        assertThat(commandGot, equalTo(methodScopedCommand));
    }

    @Test
    public void get_commandClassScoped_commandInstanceScoped_gotInstanceScopedCommand() {
        // given
        OrderedCommand classScopedCommand = new OrderedCommand(ClassContext.class.getSimpleName());
        OrderedCommand instanceScopedCommand = new OrderedCommand(InstanceContext.class.getSimpleName());
        Fixture<Command> fixture = new Fixture<>(new LinkedList<OrderedCommand>() {
            {
                add(classScopedCommand);
                add(instanceScopedCommand);
            }
        }::remove);
        fixture.redo(commandFacade, new ClassContext(getClass()));
        fixture.redo(commandFacade, new InstanceContext(this));

        // when
        Command commandGot = fixture.get();

        // then
        assertThat(commandGot, equalTo(instanceScopedCommand));
    }

    @Test
    public void get_commandClassScoped_gotClassScopedCommand() {
        // given
        OrderedCommand classScopedCommand = new OrderedCommand(ClassContext.class.getSimpleName());
        Fixture<Command> fixture = new Fixture<>(new LinkedList<OrderedCommand>() {
            {
                add(classScopedCommand);
            }
        }::remove);
        fixture.redo(commandFacade, new ClassContext(getClass()));

        // when
        Command commandGot = fixture.get();

        // then
        assertThat(commandGot, equalTo(classScopedCommand));
    }
}
