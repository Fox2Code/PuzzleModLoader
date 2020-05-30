package net.puzzle_mod_loader.events.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.puzzle_mod_loader.events.gui.GuiEvent;

public class GuiPreRenderEvent extends GuiEvent {
    private final PoseStack poseStack;

    public GuiPreRenderEvent(Screen gui, PoseStack poseStack) {
        super(gui);
        this.poseStack = poseStack;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }
}