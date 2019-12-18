package net.puzzle_mod_loader.events.server;

import net.minecraft.server.MinecraftServer;

public class ServerTickEvent extends ServerEvent {
    private final boolean haveTime;

    public ServerTickEvent(MinecraftServer minecraftServer,boolean haveTime) {
        super(minecraftServer);
        this.haveTime = haveTime;
    }

    public boolean isHaveTime() {
        return haveTime;
    }
}
