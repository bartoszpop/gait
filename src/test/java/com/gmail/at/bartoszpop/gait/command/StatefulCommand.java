package com.gmail.at.bartoszpop.gait.command;

/**
 * @author Bartosz Popiela
 */
interface StatefulCommand extends Command {
    boolean isRedone();
}
