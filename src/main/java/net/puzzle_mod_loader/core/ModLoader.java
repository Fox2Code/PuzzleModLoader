package net.puzzle_mod_loader.core;

import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.puzzle_mod_loader.helper.ModInfo;
import net.puzzle_mod_loader.helper.ModList;
import net.puzzle_mod_loader.registry.WoodRegistry;
import net.puzzle_mod_loader.launch.Launch;
import com.google.common.collect.ImmutableList;
import net.puzzle_mod_loader.utils.HashUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public class ModLoader {
    public static final Logger LOGGER = LogManager.getLogger("Puzzle");
    public static final boolean DEV_MODE = "true".equals(System.getProperty("udk.puzzle.dev-mode"));

    private static final String inject = System.getProperty("udk.puzzle.inject");
    private static final String hash;
    private static final Attributes.Name MOD_MAIN = new Attributes.Name("ModMain");
    private static final Attributes.Name MOD_ID = new Attributes.Name("ModId");
    private static final Attributes.Name MOD_NAME = new Attributes.Name("ModName");
    private static final Attributes.Name MOD_VERSION = new Attributes.Name("ModVersion");
    private static final Attributes.Name MOD_HOOK = new Attributes.Name("ModHook");
    private static HashMap<URL, String[]> cachedData = new HashMap<>();
    private static HashMap<String, Mod> mods = new HashMap<>();
    private static File modsFolder = new File(Launch.getHomeDir(), "mods");
    private static boolean modsInit = false;

    static {
        String h = "";
        try {
            h = HashUtil.md5sha1(Files.readAllBytes(new File(ModLoader.class
                    .getProtectionDomain().getCodeSource().getLocation().getFile()).toPath()));
        } catch (Exception ignored) {}
        hash = h;
    }

    static void fillContext(Mod mod) {
        String[] data = cachedData.remove(mod.getClass()
                .getProtectionDomain().getCodeSource().getLocation());
        mod.id = data[1];
        mod.name = data[2];
        mod.version = data[3];
        mod.file = new File(mod.getClass().getProtectionDomain()
                .getCodeSource().getLocation().getFile());
        mod.hash = data[5];
    }

    static void loadMods() {
        if (!modsFolder.exists()) {
            modsFolder.mkdirs();
        } else {
            LOGGER.info("Loading mods...");
            for (File file: Objects.requireNonNull(modsFolder.listFiles())) {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    try {
                        loadMod(file, false);
                    } catch (IOException e) {
                        System.err.println("Unable to load "+file.getName());
                        e.printStackTrace();
                    }
                }
            }
            if (DEV_MODE && inject != null) {
                LOGGER.info("Loading injected mod...");
                try {
                    loadMod(new File(inject), true);
                } catch (IOException e) {
                    System.err.println("Unable to load injected mod");
                    e.printStackTrace();
                    throw new Error(e);
                }
            }
            LOGGER.info(cachedData.size()+" mods loaded!");
        }
    }

    private static boolean init = false;
    static void initMods() {
        if (init) return;
        init = true;
        LOGGER.info("Building mods...");
        int i = 0;
        for (String[] modMeta:cachedData.values()) {
            if (modMeta[1] != null) try {
                mods.put(modMeta[1], Class.forName(modMeta[0]).asSubclass(Mod.class).newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final boolean client = Launch.isClient();
        LOGGER.info("Initialising mods...");
        for (Mod mod:getMods()) {
            mod.onInit();
            if (client) {
                mod.onClientInit();
            }
        }
        LOGGER.info("Updating registry...");
        modsInit = true;
        GameRegistry.init();
        WoodRegistry.parseProviders();
        LOGGER.info("Post initialising registry...");
        for (Mod mod:getMods()) {
            mod.onPostInit();
            if (client) {
                mod.onClientPostInit();
            }
        }
        LOGGER.info("All mods were initialised!");
    }

    static void loadMod(File file,boolean dev) throws IOException {
        String[] metaData = new String[6];
        metaData[5] = dev?"":HashUtil.md5sha1(Files.readAllBytes(file.toPath()));
        JarFile jarFile = new JarFile(file);
        Attributes attributes = jarFile.getManifest().getMainAttributes();
        metaData[0] = attributes.getValue(MOD_MAIN);
        metaData[1] = attributes.getValue(MOD_ID);
        if (metaData[1] == null || metaData[1].isEmpty()) throw new IllegalArgumentException("ModId not found in "+file.getName()+" !");
        if (!metaData[1].toLowerCase(Locale.ENGLISH).equals(metaData[1]) || metaData[1].equals("puzzle") || metaData[1].equals("minecraft")) {
            throw new IllegalArgumentException("\"" + metaData[1] + "\" is an invalid modId!");
        }
        metaData[2] = attributes.getValue(MOD_NAME);
        if (metaData[2] == null) metaData[2] = metaData[1].substring(0, 1).toUpperCase()+metaData[1].substring(1).toLowerCase().replace('_', ' ');
        metaData[3] = attributes.getValue(MOD_VERSION);
        if (metaData[3] == null) metaData[3] = "1.0-dev";
        metaData[4] = attributes.getValue(MOD_HOOK);
        cachedData.put(file.toURI().toURL(), metaData);
        if (!dev) {
            Launch.getClassLoader().addURL(file.toURI().toURL());
        }
        if (Launch.getClassLoader().getResource("mixins."+metaData[1]+".json") != null) {
            Mixins.addConfiguration("mixins."+metaData[1]+".json");
        }
        if (metaData[4] != null) {
            try {
                Class.forName(metaData[4]).getDeclaredMethod("hook", boolean.class).invoke(null, Launch.isClient());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Mod> getMods() {
        return ImmutableList.copyOf(mods.values());
    }

    public static boolean isInitDone() {
        return modsInit;
    }

    public static Mod getModByName(String name) {
        return mods.get(name);
    }

    static ModList getModList() {
        ModInfo[] modInfo = new ModInfo[mods.size()+1];
        modInfo[0] = new ModInfo("puzzle", "PuzzleModLoader", "1.0-alpha1", hash, "", "LOADER"+(hash.isEmpty()? ",DEV": "")+(DEV_MODE?",DEV_MODE":""));
        int i = 0;
        for (Mod mod:mods.values()) {
            modInfo[++i] = new ModInfo(mod.id, mod.name, mod.version, mod.hash, "", mod.hash.isEmpty()?"DEV":"");
        }
        return new ModList(modInfo);
    }
}
