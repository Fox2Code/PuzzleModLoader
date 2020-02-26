package net.puzzle_mod_loader.launch.tests;

public interface EnvMirror {
    void testListenerRegister() throws Throwable;
    void testEventPass() throws Throwable;
    void testEventCancel() throws Throwable;
    void testReflectStaticExpr() throws Throwable;
    void testMixin() throws Throwable;
}
