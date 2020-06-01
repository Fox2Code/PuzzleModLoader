package net.puzzle_mod_loader.core.mixins;

import net.minecraft.world.entity.Entity;
import net.puzzle_mod_loader.client.FallbackRender;
import net.puzzle_mod_loader.client.RendererManager;
import net.puzzle_mod_loader.core.ModLoader;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class EntityRendererMixin {
    @Shadow
    public Map<EntityType<?>, EntityRenderer<?>> renderers;

    @SuppressWarnings({"ConstantConditions"})
    @Inject(method = "registerRenderers",
            at = @At("HEAD"))
    private void registerRenderersPre(ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager, CallbackInfo ci) {
        this.renderers.putAll(RendererManager.entityRenderer);
        RendererManager.entityRenderer = this.renderers;
        RendererManager.dispatcher = (EntityRenderDispatcher)((Object)this);
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    @Inject(method = "registerRenderers",
            at = @At("RETURN"))
    private void registerRenderersPost(ItemRenderer itemRenderer, ReloadableResourceManager reloadableResourceManager, CallbackInfo ci) {
        RendererManager.entityRenderer = this.renderers;
        for (Map.Entry<EntityType<?>, RendererManager.EntityRendererProvider<?>> entry:RendererManager.entityRendererProviders.entrySet()) {
            //Cast are just type hack and don't affect code execution
            EntityType<Entity> type = (EntityType<Entity>) entry.getKey();
            this.renderers.put(type, ((RendererManager.EntityRendererProvider<Entity>)entry.getValue()).provide((EntityRenderDispatcher)((Object)this), type));
        }
        for (EntityType<?> entityType: Registry.ENTITY_TYPE) {
            if (entityType == EntityType.PLAYER || this.renderers.containsKey(entityType)) {
                continue;
            }
            ModLoader.LOGGER.error("No renderer registered for " + Registry.ENTITY_TYPE.getKey(entityType));
            this.renderers.put(entityType, new FallbackRender<>((EntityRenderDispatcher)((Object)this)));
        }
    }
}
