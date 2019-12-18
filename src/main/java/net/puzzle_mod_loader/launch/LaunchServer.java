package net.puzzle_mod_loader.launch;

import java.io.File;

public class LaunchServer {
    public static void main(String[] args) throws ReflectiveOperationException {
        Launch.home = new File("");
        Launch.initCore(false);
        Launch.getClassLoader().loadClass("net.minecraft.server.dedicated.DedicatedServer").getDeclaredMethod("main",String[].class).invoke(null, (Object) args);
    }
}
