package com.github.bartoszpop.gait.page;

/**
 * @author Bartosz Popiela
 */
public interface PageObjectCondition {
    boolean met();

    default PageObjectCondition and(PageObjectCondition other) {
        return () -> met() && other.met();
    }

    default PageObjectCondition or(PageObjectCondition other) {
        return () -> met() || other.met();
    }
}