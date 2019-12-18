package net.puzzle_mod_loader.events.render;

import net.puzzle_mod_loader.events.gui.GuiEvent;
import net.minecraft.client.gui.screens.Screen;

public class GuiRenderEvent extends GuiEvent {
    public GuiRenderEvent(Screen gui) {
        super(gui);
    }
}
