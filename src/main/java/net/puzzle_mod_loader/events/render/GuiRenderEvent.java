package net.puzzle_mod_loader.events.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.puzzle_mod_loader.events.gui.GuiEvent;
import net.minecraft.client.gui.screens.Screen;

public class GuiRenderEvent extends GuiEvent {
    private final PoseStack poseStack;

    public GuiRenderEvent(Screen gui,PoseStack poseStack) {
        super(gui);
        this.poseStack = poseStack;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }
}
