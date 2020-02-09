package net.puzzle_mod_loader.launch;

import net.puzzle_mod_loader.launch.rebuild.ClassData;
import net.puzzle_mod_loader.launch.transformers.*;

import java.io.File;

public class Launch {
    public static long lastLoadedClass;
    public static String lastLoadedClassName;

    public static File home;
    static PuzzleClassLoader classLoader;
    public static final CompactTransformer compactTransformer = new CompactTransformer();
    static boolean client, bukkit;

    public static File getHomeDir() {
        return home;
    }

    public static PuzzleClassLoader getClassLoader() {
        return classLoader;
    }

    public static boolean isClient() {
        return client;
    }

    public static boolean isBukkit() {
        return bukkit;
    }

    public static ClassData getClassData(String clName) {
        return classLoader.getClassDataProvider().getClassData(clName);
    }

    public static boolean hasClass(String clName) {
        return classLoader.getResource(clName.replace('.','/')+".class") != null;
    }

    public static void initCore(boolean client) throws ReflectiveOperationException {
        Launch.client = client;
        File logs = new File("logs");
        if (!logs.exists()) {
            logs.mkdirs();
        }
        classLoader = new PuzzleClassLoader(Launch.class.getClassLoader());
        classLoader.addTransformerExclusion("net.puzzle_mod_loader.core.");
        classLoader.addTransformerExclusion("net.puzzle_mod_loader.utils.");
        classLoader.addTransformerExclusion("net.puzzle_mod_loader.helper.");
        classLoader.addTransformerExclusion("org.spongepowered.asm.mixin.");
        Thread.currentThread().setContextClassLoader(classLoader);
        classLoader.loadClass("net.puzzle_mod_loader.core.mixin.MixinTransformer").getDeclaredMethod("init0").invoke(null);
        classLoader.addClassTransformers(new RegistryTransformer());
        classLoader.addClassTransformers(new DataPackTransformer());
        classLoader.addClassTransformers(new ServerTransformer());
        if (client) {
            classLoader.addClassTransformers(new ClientTransformer());
            classLoader.addClassTransformers(new RenderersTransformer());
        }
        classLoader.loadClass("net.puzzle_mod_loader.core.CoreInit").getDeclaredMethod("init").invoke(null);
    }
}
