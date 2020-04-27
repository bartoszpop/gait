package com.gmail.at.bartoszpop.gait.command;

import static java.util.Comparator.comparingInt;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Bartosz Popiela
 */
class OrderedCommand implements Command {
    private static final AtomicInteger idSequence = new AtomicInteger(0);

    private static final AtomicInteger orderSequence = new AtomicInteger(0);

    private int id = idSequence.getAndIncrement();

    private String name;

    private OrderedCommand undoCommand;

    private Integer index;

    public OrderedCommand(String name) {
        this.name = name;
    }

    public static Comparator<OrderedCommand> byIndex() {
        return comparingInt(OrderedCommand::getIndex);
    }

    public final Integer getIndex() {
        if ( index == null ) {
            throw new IllegalStateException("The command has not been redone.");
        } else {
            return index;
        }
    }

    @Override
    public final void redo() {
        if ( index != null ) {
            throw new IllegalStateException("This command has been already redone.");
        }
        index = orderSequence.getAndIncrement();
    }

    @Override
    public final OrderedCommand undo() {
        // double-check idiom
        OrderedCommand result = this.undoCommand;
        if ( result == null ) {
            synchronized (this) {
                result = this.undoCommand;
                if ( result == null ) {
                    this.undoCommand = result = new OrderedCommand("Undo " + this.name);
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "OrderedCommand{id=" + id + ",name=" + name + ",index=" + index + "}";
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;
        OrderedCommand that = (OrderedCommand) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    OrderedCommand namedAs(String name) {
        OrderedCommand command = new OrderedCommand(name);
        command.id = this.id;
        command.index = this.index;
        command.undoCommand = this.undoCommand;
        return command;
    }
}