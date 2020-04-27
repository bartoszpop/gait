package com.github.bartoszpop.gait.inject;

import javax.inject.Inject;

/**
 * @author Bartosz Popiela
 */
final class Injectee {

    @Inject
    private InjectableModule.Injectable injected;

    @Inject
    public Injectee(InjectableModule.Injectable argument) {
        this.injected = argument;
    }

    public InjectableModule.Injectable getInjected() {
        return injected;
    }
}