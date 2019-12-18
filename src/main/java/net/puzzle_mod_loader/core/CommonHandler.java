package net.puzzle_mod_loader.core;

import net.puzzle_mod_loader.events.EventManager;
import net.puzzle_mod_loader.events.server.ServerInitEvent;
import net.puzzle_mod_loader.launch.event.EventPriority;
import net.puzzle_mod_loader.launch.event.SubscribeEvent;

public class CommonHandler {
    static void init() {
        EventManager.registerListener(new CommonHandler());
    }

    @SubscribeEvent(priority = EventPriority.ASYNC)
    public void preload(ServerInitEvent event) {
        PreLoader.preload();
    }
}
