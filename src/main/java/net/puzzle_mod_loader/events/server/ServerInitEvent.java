package net.puzzle_mod_loader.events.server;

import net.minecraft.server.MinecraftServer;

public class ServerInitEvent extends ServerEvent {
    public ServerInitEvent(MinecraftServer minecraftServer) {
        super(minecraftServer);
    }
}
