package com.github.bartoszpop.gait.command;

/**
 * @author Bartosz Popiela
 */
interface StatefulCommand extends Command {
    boolean isRedone();
}
