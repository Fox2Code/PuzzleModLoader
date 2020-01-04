package net.puzzle_mod_loader.launch;

import java.io.File;

public class LaunchBukkit {
    public static void main(String[] args) throws ReflectiveOperationException {
        Launch.home = new File("");
        Launch.bukkit = true;
        Launch.initCore(false);
        Launch.getClassLoader().loadClass("org.bukkit.craftbukkit.Main").getDeclaredMethod("main",String[].class).invoke(null, (Object) args);
    }
}
