package net.puzzle_mod_loader.core.tests;

import net.puzzle_mod_loader.event.tests.TestEvent;
import net.puzzle_mod_loader.events.EventManager;
import net.puzzle_mod_loader.launch.tests.Env;
import net.puzzle_mod_loader.launch.tests.EnvMirror;

public class EnvInit implements EnvMirror {
    private Handler handler = null;

    public static void init() {
        Env.envMirror = new EnvInit();
    }

    @Override
    public void testListenerRegister() throws Throwable {
        EventManager.registerListener(this.handler = new Handler());
    }

    @Override
    public void testEventPass() throws Throwable {
        this.handler.pass = false;
        EventManager.processEvent(new TestEvent());
        if (!this.handler.pass) {
            throw new Error("TestEvent don't pass to Handler");
        }
    }

    @Override
    public void testEventCancel() throws Throwable {
        this.handler.pass = false;
        TestEvent testEvent = new TestEvent();
        testEvent.setCanceled(true);
        EventManager.processEvent(testEvent);
        if (this.handler.pass) {
            throw new Error("TestEvent shouldn't be pass to Handler if canceled");
        }
    }
}
