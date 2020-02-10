package net.puzzle_mod_loader.events.client;

import net.minecraft.network.chat.Component;

public class ClientDisconnectEvent extends ClientEvent {
    private final Component message;

    public ClientDisconnectEvent(Component message) {
        this.message = message;
    }

    public Component getMessage() {
        return message;
    }
}
