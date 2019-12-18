package net.puzzle_mod_loader.events.server;

import net.puzzle_mod_loader.events.Event;
import net.minecraft.server.MinecraftServer;

public class ServerEvent extends Event {
    private final MinecraftServer minecraftServer;

    public ServerEvent(MinecraftServer minecraftServer) {
        this.minecraftServer = minecraftServer;
    }

    public MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }
}
