package net.puzzle_mod_loader.events.client;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.network.chat.Component;

public class ClientPingEvent extends ClientEvent {
    private final ServerData serverData;
    private final ServerStatusPinger pinger;

    public ClientPingEvent(ServerData serverData,ServerStatusPinger pinger) {
        this.serverData = serverData;
        this.pinger = pinger;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public ServerStatusPinger getPinger() {
        return pinger;
    }

    public int getProtocol() {
        return this.serverData.protocol;
    }

    public void setProtocol(int protocol) {
        this.serverData.protocol = protocol;
    }

    public Component getStatus() {
        return this.serverData.status;
    }

    public void setStatus(Component status) {
        this.serverData.status = status;
    }

    public Component getVersion() {
        return this.serverData.version;
    }

    public void setVersion(Component version) {
        this.serverData.version = version;
    }
}
