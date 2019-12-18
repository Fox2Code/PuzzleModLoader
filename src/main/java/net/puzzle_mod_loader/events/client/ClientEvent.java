package net.puzzle_mod_loader.events.client;

import net.minecraft.client.Minecraft;
import net.puzzle_mod_loader.events.Event;

public class ClientEvent extends Event {
    public Minecraft getClient() {
        return Minecraft.getInstance();
    }
}
