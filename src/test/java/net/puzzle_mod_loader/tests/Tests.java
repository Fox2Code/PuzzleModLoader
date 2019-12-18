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
}
