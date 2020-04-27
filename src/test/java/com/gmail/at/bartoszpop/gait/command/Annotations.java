package com.gmail.at.bartoszpop.gait.command;

/**
 * @author Bartosz Popiela
 */
final class Annotations {
    public static String beforeClassAnnotationName() {
        return "@" + BeforeClass.class.getSimpleName();
    }

    public static String beforeInstanceAnnotationName() {
        return "@" + BeforeInstance.class.getSimpleName();
    }

    public static String beforeMethodAnnotationName() {
        return "@" + BeforeMethod.class.getSimpleName();
    }

    public static String afterLoginAnnotationName() {
        return "@" + AfterLogin.class.getSimpleName();
    }
}
