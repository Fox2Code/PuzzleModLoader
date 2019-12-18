package net.puzzle_mod_loader.events.gui;

import net.puzzle_mod_loader.events.Event;
import net.minecraft.client.gui.screens.Screen;

public class ChangeGuiEvent extends Event implements Event.Cancelable {
    private final Screen from;
    private Screen to;

    public ChangeGuiEvent(Screen from,Screen to) {
        this.from = from;
        this.to = to;
    }

    public Screen getFrom() {
        return from;
    }

    public Screen getTo() {
        return to;
    }

    public void setTo(Screen to) {
        this.to = to;
    }
}
