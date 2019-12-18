package net.puzzle_mod_loader.core;

import net.puzzle_mod_loader.compact.Constructor;
import net.puzzle_mod_loader.compact.Implement;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.CountRangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import java.util.LinkedList;
import java.util.function.Supplier;

public class GameRegistry {
    private static LinkedList<Runnable> awaitTasks = new LinkedList<>();

    static void init() {
        awaitTasks.forEach(Runnable::run);
    }

    public static Item registerBlock(ResourceLocation var0, Block var1, CreativeModeTab var2) {
        registerBlock0(var0, var1);
        return registerItem0(var0, new BlockItem(var1, (new Item.Properties()).tab(var2)));
    }

    public static Item registerBlock(ResourceLocation var0, Block var1) {
        registerBlock0(var0, var1);
        return registerItem0(var0, new BlockItem(var1, (new Item.Properties())));
    }

    public static Item registerBlock(ResourceLocation var0, Block var1, Item var2) {
        registerBlock0(var0, var1);
        return registerItem0(var0, var2);
    }

    public static Item registerItem(ResourceLocation var0, Item var1) {
        return registerItem0(var0, var1);
    }

    private static void registerBlock0(ResourceLocation var0, Block var1) {
        Registry.register(Registry.BLOCK, var0, var1);
    }

    private static Item registerItem0(ResourceLocation var0, Item var1) {
        if (var1 instanceof BlockItem) {
            ((BlockItem)var1).registerBlocks(Item.BY_BLOCK, var1);
        }

        return Registry.register(Registry.ITEM, var0, var1);
    }

    public static <T extends AbstractContainerMenu> MenuType<T> registerContainer(ResourceLocation var0, MenuSupplier<T> var1) {
        return Registry.register(Registry.MENU, var0, newMenuType(var1));
    }

    @Constructor("(Lnet/minecraft/world/inventory/MenuType$MenuSupplier;)V")
    private static <T extends AbstractContainerMenu> MenuType<T> newMenuType(MenuSupplier<T> menuSupplier) {
        return null;
    }

    @Implement("net.minecraft.world.inventory.MenuType$MenuSupplier")
    public interface MenuSupplier<T extends AbstractContainerMenu> {
        T create(int var1, Inventory var2);
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerTile(ResourceLocation resourceLocation, Supplier<T> var0, Block... var1) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, resourceLocation, BlockEntityType.Builder.of(var0, var1).build(null));
    }

    public static void registerOreGen(BlockState ORE, int maxSize, int maxVein, int maxY) {
        if (!ModLoader.isInitDone()) {
            awaitTasks.add(() -> registerOreGen(ORE, maxSize, maxVein, maxY));
            return;
        }
        if (Biome.EXPLORABLE_BIOMES.isEmpty()) {
            Biomes.OCEAN.getClass();
        }
        for (Biome biome:Registry.BIOME) {
            biome.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreConfiguration(OreConfiguration.Predicates.NATURAL_STONE, ORE, maxSize)).decorated(FeatureDecorator.COUNT_RANGE.configured(new CountRangeDecoratorConfiguration(maxVein, 0, 0, maxY))));
        }
    }

    public static void registerBiome(ResourceLocation resourceLocation,Biome biome) {
        if (Biome.EXPLORABLE_BIOMES.isEmpty()) {
            Biomes.OCEAN.getClass();
        }
        Registry.register(Registry.BIOME, resourceLocation, biome);
    }

    public static <T extends Entity> EntityType<T> registerEntity(ResourceLocation resourceLocation, EntityType.EntityFactory<T> entityFactory, MobCategory mobCategory, EntityDimensions dimensions, boolean save, boolean fireImmune, boolean canSummon, boolean canSpawnFarFromPlayer) {
        return registerEntity(resourceLocation, new EntityType<>(entityFactory, mobCategory, save, canSummon, fireImmune, canSpawnFarFromPlayer, dimensions));
    }

    public static <T extends Entity> EntityType<T> registerEntity(ResourceLocation resourceLocation, EntityType<T> entityType) {
        return Registry.register(Registry.ENTITY_TYPE, resourceLocation, entityType);
    }

    private static final EntityDimensions DEFAULT_ENTITY_DIMENSIONS = EntityDimensions.scalable(0.6F, 0.7F);

    public static class EntityTypeBuilder<T extends Entity> {
        private final ResourceLocation resourceLocation;
        private final EntityType.EntityFactory<T> entityFactory;
        private final MobCategory mobCategory;
        private EntityDimensions entityDimensions;
        private boolean save, fireImmune, canSummon, canSpawnFarFromPlayer;

        public EntityTypeBuilder(ResourceLocation resourceLocation, EntityType.EntityFactory<T> entityFactory, MobCategory mobCategory) {
            this.resourceLocation = resourceLocation;
            this.entityFactory = entityFactory;
            this.mobCategory = mobCategory;
            this.entityDimensions = DEFAULT_ENTITY_DIMENSIONS;
            this.save = true;
            this.fireImmune = false;
            this.canSummon = true;
            this.canSpawnFarFromPlayer = mobCategory == MobCategory.CREATURE || mobCategory == MobCategory.MISC;
        }

        public EntityTypeBuilder<T> setDimensions(EntityDimensions entityDimensions) {
            this.entityDimensions = entityDimensions;
            return this;
        }

        public EntityTypeBuilder<T> setDimensions(float width,float height) {
            this.entityDimensions = EntityDimensions.scalable(width, height);
            return this;
        }

        public EntityTypeBuilder<T> setCanSpawnFarFromPlayer(boolean canSpawnFarFromPlayer) {
            this.canSpawnFarFromPlayer = canSpawnFarFromPlayer;
            return this;
        }

        public EntityTypeBuilder<T> noSummon() {
            this.canSummon = false;
            return this;
        }

        public EntityTypeBuilder<T> noSave() {
            this.save = false;
            return this;
        }

        public EntityTypeBuilder<T> fireImmune() {
            this.fireImmune = true;
            return this;
        }

        public EntityType<T> register() {
            return registerEntity(resourceLocation, entityFactory, mobCategory, entityDimensions, save, fireImmune, canSummon, canSpawnFarFromPlayer);
        }
    }
}
