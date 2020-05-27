package net.puzzle_mod_loader.tests;

import net.puzzle_mod_loader.launch.tests.Env;
import org.junit.Test;

public class Tests {
    @Test
    public void envInit() throws Throwable {
        Env.getEnvMirror();
    }

    @Test
    public void testListenerRegister() throws Throwable {
        Env.getEnvMirror().testListenerRegister();
    }

    @Test
    public void testEventPass() throws Throwable {
        Env.getEnvMirror().testEventPass();
    }

    @Test
    public void testEventCancel() throws Throwable {
        Env.getEnvMirror().testEventCancel();
    }

    @Test
    public void testReflectStaticExpr() throws Throwable {
        Env.getEnvMirror().testReflectStaticExpr();
    }

    @Test
    public void testReflectEnumAdd() throws Throwable {
        Env.getEnvMirror().testReflectEnumAdd();
    }

    @Test
    public void testMixin() throws Throwable {
        Env.getEnvMirror().testMixin();
    }

    @Test
    public void testCompactImplement() throws Throwable {
        Env.getEnvMirror().testCompactImplement();
    }
}
