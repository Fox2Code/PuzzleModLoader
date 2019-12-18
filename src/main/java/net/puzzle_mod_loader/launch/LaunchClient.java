package net.puzzle_mod_loader.launch;

import java.io.File;

public class LaunchClient {
    public static void main(String[] args) throws ReflectiveOperationException {
        for (int i = 0;i < args.length;i++) {
            if (args[i].equals("--gameDir")) Launch.home = new File(args[i+1]);
        }
        Launch.initCore(true);
        Launch.getClassLoader().loadClass("net.minecraft.client.main.Main").getDeclaredMethod("main",String[].class).invoke(null, (Object) args);
    }
}
