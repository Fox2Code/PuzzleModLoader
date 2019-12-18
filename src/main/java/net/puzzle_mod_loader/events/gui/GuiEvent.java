package net.puzzle_mod_loader.events.gui;

import net.puzzle_mod_loader.events.Event;
import net.minecraft.client.gui.screens.Screen;

public class GuiEvent extends Event {
    private final Screen gui;

    public GuiEvent(Screen gui) {
        this.gui = gui;
    }

    public Screen getGui() {
        return gui;
    }
}
