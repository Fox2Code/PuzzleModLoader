package net.puzzle_mod_loader.core;

import com.mojang.authlib.AuthenticationService;
import com.mojang.bridge.Bridge;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.DataFixerUpper;
import net.minecraft.server.MinecraftServer;
import net.puzzle_mod_loader.compact.ClientOnly;
import net.puzzle_mod_loader.launch.Launch;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Used to preload minecraft classes
 */
final class PreLoader {
    static boolean preloading = false;
    static boolean preloaded = false;


    static void preloadAsync() {
        if (preloading || preloaded) {
            return;
        }
        new Thread(PreLoader::preload, "Async - Preload").start();
    }

    static void preload() {
        if (preloading || preloaded) {
            return;
        }
        preloading = true;
        ModLoader.LOGGER.info("Preloading classes...");
        preloadJar(new File(MinecraftServer.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getAbsoluteFile(), true);
        if (Launch.isClient()) {
            preloadClient();
        }
        ModLoader.LOGGER.info("All game classes are preloaded!");
        preloaded = true;
        preloading = false;
    }

    @ClientOnly
    static void preloadClient() {
        preloadJar(new File(AuthenticationService.class.getProtectionDomain().getCodeSource().getLocation().getFile()), false);
        preloadJar(new File(Bridge.class.getProtectionDomain().getCodeSource().getLocation().getFile()), false);
        preloadJar(new File(CommandDispatcher.class.getProtectionDomain().getCodeSource().getLocation().getFile()), false);
        preloadJar(new File(DataFixerUpper.class.getProtectionDomain().getCodeSource().getLocation().getFile()), false);
    }

    static void preloadJar(File file, boolean mc) {
        try (ZipFile zipFile = new ZipFile(file)){
            Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry zipEntry = enumeration.nextElement();
                if (!zipEntry.getName().endsWith(".class")) {
                    continue;
                }
                if (mc && !(zipEntry.getName().startsWith("net/minecraft/") || zipEntry.getName().startsWith("com/mojang/"))) {
                    continue;
                }
                try {
                    Class.forName(zipEntry.getName().substring(0, zipEntry.getName().length()-6).replace('.', '/'));
                } catch (Throwable ignored) {}
            }
        } catch (IOException ignored) {}
    }
}
