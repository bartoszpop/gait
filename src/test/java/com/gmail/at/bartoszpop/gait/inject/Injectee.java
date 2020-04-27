package com.gmail.at.bartoszpop.gait.inject;

import javax.inject.Inject;

import com.gmail.at.bartoszpop.gait.inject.InjectableModule.Injectable;

/**
 * @author Bartosz Popiela
 */
final class Injectee {

    @Inject
    private Injectable injected;

    @Inject
    public Injectee(Injectable argument) {
        this.injected = argument;
    }

    public Injectable getInjected() {
        return injected;
    }
}