package net.puzzle_mod_loader.core.tests;

import net.puzzle_mod_loader.core.ModLoader;
import net.puzzle_mod_loader.core.mixin.MixinTransformer;
import net.puzzle_mod_loader.event.tests.TestEvent;
import net.puzzle_mod_loader.events.EventManager;
import net.puzzle_mod_loader.launch.tests.Env;
import net.puzzle_mod_loader.launch.tests.EnvMirror;
import net.puzzle_mod_loader.tests.MixinTest;
import net.puzzle_mod_loader.utils.ReflectedClass;
import org.spongepowered.asm.mixin.Mixins;

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
        this.handler.passAsync = false;
        EventManager.processEvent(new TestEvent());
        if (!this.handler.pass) {
            throw new Error("TestEvent don't pass to Handler");
        }
        if (!this.handler.passAsync) {
            throw new Error("TestEvent don't pass to Async Handler");
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

    @Override
    public void testReflectStaticExpr() throws Throwable {
        ReflectTest.testReflectStaticExpr();
    }

    @Override
    public void testMixin() throws Throwable {
        Mixins.addConfiguration("mixins.puzzle.tests.json");
        ModLoader.LOGGER.info("Configs: "+Mixins.getConfigs());
        MixinTransformer.forceReload(); // Force reload the configs
        MixinTest.test();
    }

    @Override
    public void testCompactImplement() throws Throwable {
        CompactTest.testImplement();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void testReflectEnumAdd() throws Throwable {
        if (ReflectedClass.of(StdEnum.class).addEnum("VALUE3") == null) {
            throw new Error("Return value is null!");
        }
        if (StdEnum.valueOf("VALUE3") == null) {
            throw new Error("VALUE3 is not correctly registered in StdEnum!");
        }
    }
}
