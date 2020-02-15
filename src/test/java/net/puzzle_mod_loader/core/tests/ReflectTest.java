package net.puzzle_mod_loader.core.tests;

import net.puzzle_mod_loader.utils.ReflectedClass;

public class ReflectTest {
    public static Object staticObject = new Object();
    public static boolean staticCall;

    public static void testReflectStaticExpr() throws Throwable {
        staticCall = false;
        ReflectedClass.$(ReflectTest.class, "ReflectTest.staticMethod()");
        if (!staticCall) {
            throw new Error("staticMethod() not called");
        }
        if (ReflectedClass.$(ReflectTest.class, "ReflectTest#staticObject").getHandle() != staticObject) {
            throw new Error("Mismatch while getting #staticObject");
        }
    }

    public static void staticMethod() throws Throwable {
        staticCall = true;
    }
}
