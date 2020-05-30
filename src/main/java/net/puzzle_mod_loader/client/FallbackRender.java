package net.puzzle_mod_loader.client;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class FallbackRender<T extends Entity> extends EntityRenderer<T> {
    public FallbackRender(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public ResourceLocation getTextureLocation(T t) {
        return TextureManager.INTENTIONAL_MISSING_TEXTURE;
    }

    @Override
    public int getBlockLightLevel(T entity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public boolean shouldShowName(T t) {
        return true;
    }
}
