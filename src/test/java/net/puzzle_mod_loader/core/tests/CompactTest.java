package net.puzzle_mod_loader.core.tests;

import net.puzzle_mod_loader.compact.Implement;

@SuppressWarnings("ConstantConditions")
public class CompactTest {
    static void testImplement() throws Throwable {
        ((InterfaceTest)new ClassTest()).getClass();
    }

    public interface InterfaceTest {}

    @Implement("net/puzzle_mod_loader/core/tests/CompactTest$InterfaceTest")
    public static class ClassTest {}
}
