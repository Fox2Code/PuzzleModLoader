package net.puzzle_mod_loader.events.client;

import net.minecraft.client.multiplayer.ServerData;

public class ClientPreJoinEvent extends ClientEvent {
    private final ServerData serverData;
    private int protocol;

    public ClientPreJoinEvent(ServerData serverData,int protocol) {
        this.serverData = serverData;
        this.protocol = protocol;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }
}
