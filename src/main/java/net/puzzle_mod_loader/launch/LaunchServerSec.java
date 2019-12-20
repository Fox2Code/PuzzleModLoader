package net.puzzle_mod_loader.launch;

import java.io.File;

/**
 * When launching the server from a client environment
 */
public class LaunchServerSec {
    public static void main(String[] args) throws ReflectiveOperationException {
        for (int i = 0;i < args.length;i++) {
            if (args[i].equals("--gameDir")) Launch.home = new File(args[i+1]);
        }
        System.setProperty("user.dir", Launch.home.getAbsolutePath());
        Launch.initCore(false);
        Launch.getClassLoader().loadClass("net.minecraft.server.dedicated.DedicatedServer").getDeclaredMethod("main",String[].class).invoke(null, (Object) args);
    }
}
