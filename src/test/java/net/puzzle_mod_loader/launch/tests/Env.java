package net.puzzle_mod_loader.launch.tests;

import net.puzzle_mod_loader.launch.Launch;

import java.io.File;

public class Env {
    public static EnvMirror envMirror;

    public static EnvMirror getEnvMirror() throws ReflectiveOperationException {
        if (envMirror != null) {
            return envMirror;
        }
        Launch.home = new File(".").getAbsoluteFile();
        Launch.initCore(false);
        Launch.getClassLoader().loadClass("net.puzzle_mod_loader.core.tests.EnvInit").getDeclaredMethod("init").invoke(null);
        return envMirror;
    }
}
