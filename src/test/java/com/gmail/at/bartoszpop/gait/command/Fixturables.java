package com.gmail.at.bartoszpop.gait.command;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bartosz Popiela
 */
final class Fixturables {

    private Fixturables() {
    }

    public static String beforeClassFixturableMethodName() {
        Fixturable fixturable = new Fixturable() {
            @Override
            public List<Fixture<?>> beforeClass() {
                return new ArrayList<Fixture<?>>() {
                };
            }
        };
        String methodName = fixturable.beforeClass()
                .getClass()
                .getEnclosingMethod()
                .getName();
        return Fixturable.class.getSimpleName() + "#" + methodName + "()";
    }

    public static String beforeInstanceFixturableMethodName() {
        Fixturable fixturable = new Fixturable() {
            @Override
            public List<Fixture<?>> beforeInstance() {
                return new ArrayList<Fixture<?>>() {
                };
            }
        };
        String methodName = fixturable.beforeInstance()
                .getClass()
                .getEnclosingMethod()
                .getName();
        return Fixturable.class.getSimpleName() + "#" + methodName + "()";
    }

    public static String beforeMethodFixturableMethodName() {
        Fixturable fixturable = new Fixturable() {
            @Override
            public List<Fixture<?>> beforeMethod() {
                return new ArrayList<Fixture<?>>() {
                };
            }
        };
        String methodName = fixturable.beforeMethod()
                .getClass()
                .getEnclosingMethod()
                .getName();
        return Fixturable.class.getSimpleName() + "#" + methodName + "()";
    }
}