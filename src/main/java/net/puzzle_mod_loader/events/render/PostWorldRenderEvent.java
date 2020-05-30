package net.puzzle_mod_loader.events.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.puzzle_mod_loader.events.Event;

public class PostWorldRenderEvent extends Event {
    private final PoseStack poseStack;

    public PostWorldRenderEvent(PoseStack poseStack) {
        this.poseStack = poseStack;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }
}
