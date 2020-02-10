package net.puzzle_mod_loader.events.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;

public class ClientJoinEvent extends ClientEvent {
    private final ServerData serverData;
    private final ClientPacketListener packetListener;

    public ClientJoinEvent(ServerData serverData, ClientPacketListener packetListener) {
        this.serverData = serverData;
        this.packetListener = packetListener;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public ClientPacketListener getPacketListener() {
        return packetListener;
    }

    public ClientLevel getWorld() {
        return getClient().level;
    }
}
