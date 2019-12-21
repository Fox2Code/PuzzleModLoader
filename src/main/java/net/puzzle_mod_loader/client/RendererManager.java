package net.puzzle_mod_loader.client;

import com.fox2code.udk.startup.Internal;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class RendererManager {
    public static Map<EntityType<?>, EntityRenderer<?>> entityRenderer = new HashMap<>();
    public static EntityRenderDispatcher dispatcher;
    @Internal
    public static Map<EntityType<?>, EntityRendererProvider<?>> entityRendererProviders = new HashMap<>();

    public interface EntityRendererProvider<T extends Entity> {
        EntityRenderer<T> provide(EntityRenderDispatcher entityRenderDispatcher, EntityType<T> entityType);
    }
}
