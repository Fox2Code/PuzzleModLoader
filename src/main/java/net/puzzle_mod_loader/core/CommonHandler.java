package net.puzzle_mod_loader.core;

import net.minecraft.network.Connection;
import net.puzzle_mod_loader.events.EventManager;
import net.puzzle_mod_loader.events.server.ServerInitEvent;
import net.puzzle_mod_loader.launch.Launch;
import net.puzzle_mod_loader.launch.event.EventPriority;
import net.puzzle_mod_loader.launch.event.SubscribeEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

public class CommonHandler {
    static void init() {
        EventManager.registerListener(new CommonHandler());
        if (Mod.DEV_ENV || Launch.isClient()) {
            Configurator.setLevel(LogManager.getLogger(Connection.class).getName(), Level.DEBUG);
        }
    }

    @SubscribeEvent(priority = EventPriority.ASYNC)
    public void preload(ServerInitEvent event) {
        PreLoader.preload();
    }
}
