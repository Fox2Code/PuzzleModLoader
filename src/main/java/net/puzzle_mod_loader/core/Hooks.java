package net.puzzle_mod_loader.core;

import net.puzzle_mod_loader.events.EventManager;
import net.puzzle_mod_loader.events.server.ServerInitEvent;
import net.puzzle_mod_loader.events.server.ServerTickEvent;
import net.puzzle_mod_loader.launch.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.UnopenedPack;

import java.util.Map;

public class Hooks {
    public static void registryHook() {
        ModLoader.initMods();
        CommonHandler.init();
        if (Launch.isClient()) {
            ClientHandler.init();
        }
    }

    /**
     * {@code dataPackHook} is also used as resource pack hook
     */
    public static <T extends UnopenedPack> void dataPackHook(Map<String, T> var1, UnopenedPack.UnopenedPackConstructor<T> var2) {
        ModLoader.LOGGER.info("Injecting Data Pack...");
        for (Mod mod:ModLoader.getMods()) {
            String id = "mod/"+mod.id;
            T var3 = UnopenedPack.create(id, false, () -> mod.dataPack,
                    var2, UnopenedPack.Position.BOTTOM);
            if (var3 != null) {
                var1.put(id, var3);
            }
        }
    }

    public static void onServerInit(MinecraftServer minecraftServer) {
        EventManager.processEvent(new ServerInitEvent(minecraftServer));
    }

    public static void onServerTick(MinecraftServer minecraftServer,boolean haveTime) {
        minecraftServer.getProfiler().push("puzzle");
        EventManager.processEvent(new ServerTickEvent(minecraftServer, haveTime));
        minecraftServer.getProfiler().pop();
    }
}
