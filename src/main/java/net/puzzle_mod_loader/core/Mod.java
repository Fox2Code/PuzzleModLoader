package net.puzzle_mod_loader.core;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.puzzle_mod_loader.client.RendererManager;
import net.puzzle_mod_loader.compact.ClientOnly;
import net.puzzle_mod_loader.utils.CustomOreBlock;
import net.puzzle_mod_loader.utils.CustomStairBlock;
import net.puzzle_mod_loader.utils.ModDataPack;
import net.puzzle_mod_loader.events.EventManager;
import net.puzzle_mod_loader.launch.Launch;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.puzzle_mod_loader.utils.ReflectedClass;

import java.io.File;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class Mod extends ReflectedClass.Reflective {
    public static final boolean CLIENT = Launch.isClient();
    public static final boolean BUKKIT = Launch.isBukkit();
    public static final boolean DEV_ENV = "true".equalsIgnoreCase(System.getProperty("udk.startup.init"));

    String id;
    String name;
    String version;
    String hash;
    ModDataPack dataPack;
    File file;
    int recipeID = 0;

    public Mod() {
        if (this.getClass().getName().startsWith("com.fox2code.puzzle.")
                ||this.getClass().getName().startsWith("net.minecraft.")) {
            throw new Error("Invalid mod package!");
        }
        ModLoader.fillContext(this);
        this.dataPack = new ModDataPack(this);
    }

    public final String getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final String getVersion() {
        return version;
    }

    public final ModDataPack getDataPack() {
        return dataPack;
    }

    public final File getFile() {
        return file;
    }

    public final void injectResource(String path, String data) {
        System.out.println("Injecting => "+path);
        dataPack.injectResource(path, data);
    }

    public final void injectResource(String path, byte[] data) {
        dataPack.injectResource(path, data);
    }

    public final void registerEventListener(Object object) {
        EventManager.registerListener(object);
    }

    public final Item registerBlock(String id, Block block) {
        return GameRegistry.registerBlock(new ResourceLocation(this.id, id), block);
    }

    public final Item registerBlock(String id, Block block, CreativeModeTab creativeModeTab) {
        return GameRegistry.registerBlock(new ResourceLocation(this.id, id), block, creativeModeTab);
    }

    public final Item registerBlock(String id, Block block, Item item) {
        return GameRegistry.registerBlock(new ResourceLocation(this.id, id), block, item);
    }

    public final Item registerItem(String id, Item block) {
        return GameRegistry.registerItem(new ResourceLocation(this.id, id), block);
    }

    public final <T extends AbstractContainerMenu> MenuType<T> registerContainer(String var0, GameRegistry.MenuSupplier<T> menuSupplier) {
        return GameRegistry.registerContainer(new ResourceLocation(this.id, var0), menuSupplier);
    }

    public <T extends BlockEntity> BlockEntityType<T> registerTile(String var0, Supplier<T> var1, Block... var2) {
        return GameRegistry.registerTile(new ResourceLocation(this.id, var0), var1, var2);
    }

    public final void registerOreGen(Block ORE, int maxSize, int maxVein, int maxY) {
        registerOreGen(ORE.defaultBlockState(), maxSize, maxVein, maxY);
    }

    public final void registerOreGen(BlockState ORE, int maxSize, int maxVein, int maxY) {
        GameRegistry.registerOreGen(ORE, maxSize, maxVein, maxY);
    }

    public final void registerBiome(String id,Biome biome) {
        GameRegistry.registerBiome(new ResourceLocation(this.id, id), biome);
    }

    public final <T extends Entity> EntityType<T> registerEntity(String id, EntityType.EntityFactory<T> entityFactory, MobCategory mobCategory, EntityDimensions entityDimensions) {
        return GameRegistry.registerEntity(new ResourceLocation(this.id, id), entityFactory, mobCategory, entityDimensions, true, false, true, mobCategory == MobCategory.CREATURE || mobCategory == MobCategory.MISC);
    }

    public final <T extends Entity> GameRegistry.EntityTypeBuilder<T> newEntityTypeBuilder(String id, EntityType.EntityFactory<T> entityFactory, MobCategory mobCategory, EntityDimensions entityDimensions) {
        return newEntityTypeBuilder(id, entityFactory, mobCategory).setDimensions(entityDimensions);
    }

    public final <T extends Entity> GameRegistry.EntityTypeBuilder<T> newEntityTypeBuilder(String id, EntityType.EntityFactory<T> entityFactory, MobCategory mobCategory) {
        return new GameRegistry.EntityTypeBuilder<>(new ResourceLocation(this.id, id), entityFactory, mobCategory);
    }

    public void onInit() {}

    @ClientOnly
    public void onClientInit() {}

    public void onPostInit() {}

    @ClientOnly
    public void onClientPostInit() {}

    /////////////////////////
    // CLIENT ONLY METHODS //
    /////////////////////////

    @ClientOnly
    public <T extends Entity> void registerEntityRender(EntityType<T> type, EntityRenderer<T> entityRenderer) {
        if (RendererManager.dispatcher != null) {
            ModLoader.LOGGER.warn("Mod "+this.id+" registered entity render a bit too late!");
            ModLoader.LOGGER.warn("The entity renderer will still be added but you should ask the mod author to do this more early if possible!");
        }
        RendererManager.entityRenderer.put(type, entityRenderer);
    }

    @ClientOnly
    public <T extends Entity> void registerEntityRender(EntityType<T> type, RendererManager.EntityRendererProvider<T> provider) {
        if (RendererManager.dispatcher != null) {
            ModLoader.LOGGER.warn("Mod "+this.id+" registered entity render a bit too late!");
            ModLoader.LOGGER.warn("The entity renderer will still be added but you should ask the mod author to do this more early if possible!");
            RendererManager.entityRenderer.put(type, provider.provide(RendererManager.dispatcher, type));
        } else {
            RendererManager.entityRendererProviders.put(type, provider);
        }
    }

    //////////////////
    // EASY METHODS //
    //////////////////

    public Item easyItem(String id) {
        return this.easyItem(id, CreativeModeTab.TAB_MATERIALS);
    }

    public Item easyItem(String id,CreativeModeTab tab) {
        return this.easyItem(id, new Item.Properties().tab(tab));
    }

    public Item easyItem(String id,Item.Properties properties) {
        Item ITEM;
        this.registerItem(id, ITEM = new Item(properties));
        this.registerItemModel(ITEM);
        return ITEM;
    }

    public Block easyBlock(String id, Material var0, MaterialColor var1) {
        return this.easyBlock(id, CreativeModeTab.TAB_BUILDING_BLOCKS, var0, var1);
    }

    public Block easyBlock(String id,CreativeModeTab tab, Material var0, MaterialColor var1) {
        return this.easyBlock(id, Block.Properties.of(var0, var1), tab);
    }

    public Block easyBlock(String id,Block.Properties properties) {
        return this.easyBlock(id, properties, CreativeModeTab.TAB_BUILDING_BLOCKS);
    }

    public Block easyBlock(String id,Block.Properties properties,CreativeModeTab tab) {
        Block BLOCK = new Block(properties);
        this.registerBlock(id, BLOCK, tab);
        this.registerBlockModel(BLOCK);
        this.registerDrop(BLOCK);
        return BLOCK;
    }

    public Block easySlab(String id,Block from, Material var0, MaterialColor var1) {
        return this.easySlab(id, from, CreativeModeTab.TAB_BUILDING_BLOCKS, var0, var1);
    }

    public Block easySlab(String id,Block from,CreativeModeTab tab, Material var0, MaterialColor var1) {
        return this.easySlab(id, from, Block.Properties.of(var0, var1), tab);
    }

    public Block easySlab(Block from) {
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(from);
        return this.easySlab(resourceLocation.getPath()+"_slab", from);
    }

    public Block easySlab(Block from,CreativeModeTab tab) {
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(from);
        return this.easySlab(resourceLocation.getPath()+"_slab", from, tab);
    }

    public Block easySlab(String id,Block from) {
        return this.easySlab(id, from, Block.Properties.copy(from));
    }

    public Block easySlab(String id,Block from,CreativeModeTab tab) {
        return this.easySlab(id, from, Block.Properties.copy(from));
    }

    public Block easySlab(String id,Block from,Block.Properties properties) {
        return this.easySlab(id, from, properties, CreativeModeTab.TAB_BUILDING_BLOCKS);
    }

    public Block easySlab(String id,Block from,Block.Properties properties,CreativeModeTab tab) {
        Block BLOCK = new SlabBlock(properties);
        this.registerBlock(id, BLOCK, tab);
        this.registerSlabModel(BLOCK, from);
        this.registerSlabDrop(BLOCK);
        this.registerShapedRecipe(new String[]{"XXX"}, new ItemStack(BLOCK, 6), 'X', from);
        return BLOCK;
    }

    public Block easyStair(String id,Block from, Material var0, MaterialColor var1) {
        return this.easyStair(id, from, CreativeModeTab.TAB_BUILDING_BLOCKS, var0, var1);
    }

    public Block easyStair(String id,Block from,CreativeModeTab tab, Material var0, MaterialColor var1) {
        return this.easyStair(id, from, Block.Properties.of(var0, var1), tab);
    }

    public Block easyStair(Block from) {
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(from);
        return this.easyStair(resourceLocation.getPath()+"_stair", from);
    }

    public Block easyStair(Block from,CreativeModeTab tab) {
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(from);
        return this.easyStair(resourceLocation.getPath()+"_stair", from, tab);
    }

    public Block easyStair(String id,Block from) {
        return this.easyStair(id, from, Block.Properties.copy(from));
    }

    public Block easyStair(String id,Block from,CreativeModeTab tab) {
        return this.easyStair(id, from, Block.Properties.copy(from));
    }

    public Block easyStair(String id,Block from,Block.Properties properties) {
        return this.easyStair(id, from, properties, CreativeModeTab.TAB_BUILDING_BLOCKS);
    }

    public Block easyStair(String id,Block from,Block.Properties properties,CreativeModeTab tab) {
        Block BLOCK = new CustomStairBlock(from.defaultBlockState(), properties);
        this.registerBlock(id, BLOCK, tab);
        this.registerStairModel(BLOCK, from);
        this.registerDrop(BLOCK);
        this.registerShapedRecipe(new String[]{"  X", " XX", "XXX"}, new ItemStack(BLOCK, 4), 'X', from);
        this.registerShapedRecipe(new String[]{"X  ", "XX ", "XXX"}, new ItemStack(BLOCK, 4), 'X', from);
        return BLOCK;
    }

    public Block easyOre(String id, Block.Properties properties,Item drop, int maxSize, int maxVein, int maxY,int minXP,int maxXP) {
        return this.easyOre(id, properties, CreativeModeTab.TAB_BUILDING_BLOCKS, drop, maxSize, maxVein, maxY, minXP, maxXP);
    }

    public Block easyOre(String id, Block.Properties properties,CreativeModeTab tab,Item drop, int maxSize, int maxVein, int maxY,int minXP,int maxXP) {
        return this.easyOre(id, properties, tab, drop, maxSize, maxVein, maxY, minXP, maxXP, true);
    }

    public Block easyOre(String id, Block.Properties properties,Item drop, int maxSize, int maxVein, int maxY,int minXP,int maxXP,boolean dropOre) {
        return this.easyOre(id, properties, CreativeModeTab.TAB_BUILDING_BLOCKS, drop, maxSize, maxVein, maxY, minXP, maxXP, dropOre);
    }

    public Block easyOre(String id, Block.Properties properties,CreativeModeTab tab,Item drop, int maxSize, int maxVein, int maxY,int minXP,int maxXP,boolean dropOre) {
        Block BLOCK = new CustomOreBlock(properties, minXP, maxXP);
        this.registerBlock(id, BLOCK, tab);
        this.registerBlockModel(BLOCK);
        if (dropOre) {
            this.registerOreDrop(BLOCK, drop);
        } else {
            this.registerDrop(BLOCK);
        }
        this.registerFurnaceRecipe(BLOCK, drop, (minXP+maxXP)/2F);
        return BLOCK;
    }

    ////////////////////////////////
    // RESOURCES INJECTOR METHODS //
    ////////////////////////////////

    public final void registerOreDrop(Block var1, Item drop) {
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(var1);
        ResourceLocation item = Registry.ITEM.getKey(drop);
        this.injectResource("data/"+resourceLocation.getNamespace()+"/loot_tables/blocks/"+resourceLocation.getPath()+".json","{\n" +
                "  \"type\": \"minecraft:block\",\n" +
                "  \"pools\": [\n" +
                "    {\n" +
                "      \"rolls\": 1,\n" +
                "      \"entries\": [\n" +
                "        {\n" +
                "          \"type\": \"minecraft:alternatives\",\n" +
                "          \"children\": [\n" +
                "            {\n" +
                "              \"type\": \"minecraft:item\",\n" +
                "              \"conditions\": [\n" +
                "                {\n" +
                "                  \"condition\": \"minecraft:match_tool\",\n" +
                "                  \"predicate\": {\n" +
                "                    \"enchantments\": [\n" +
                "                      {\n" +
                "                        \"enchantment\": \"minecraft:silk_touch\",\n" +
                "                        \"levels\": {\n" +
                "                          \"min\": 1\n" +
                "                        }\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                }\n" +
                "              ],\n" +
                "              \"name\": \""+resourceLocation.toString()+"\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"type\": \"minecraft:item\",\n" +
                "              \"functions\": [\n" +
                "                {\n" +
                "                  \"function\": \"minecraft:apply_bonus\",\n" +
                "                  \"enchantment\": \"minecraft:fortune\",\n" +
                "                  \"formula\": \"minecraft:ore_drops\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"function\": \"minecraft:explosion_decay\"\n" +
                "                }\n" +
                "              ],\n" +
                "              \"name\": \""+item.toString()+"\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}");
    }

    public final void registerDrop(Block var1, ItemLike drop) {
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(var1);
        ResourceLocation item = Registry.ITEM.getKey(drop.asItem());
        this.injectResource("data/"+resourceLocation.getNamespace()+"/loot_tables/blocks/"+resourceLocation.getPath()+".json","{\n" +
                "  \"type\": \"minecraft:block\",\n" +
                "  \"pools\": [\n" +
                "    {\n" +
                "      \"rolls\": 1,\n" +
                "      \"entries\": [\n" +
                "        {\n" +
                "          \"type\": \"minecraft:item\",\n" +
                "          \"name\": \""+item.toString()+"\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"conditions\": [\n" +
                "        {\n" +
                "          \"condition\": \"minecraft:survives_explosion\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}");
    }

    public final void registerDrop(Block var1) {
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(var1);
        this.injectResource("data/"+resourceLocation.getNamespace()+"/loot_tables/blocks/"+resourceLocation.getPath()+".json","{\n" +
                "  \"type\": \"minecraft:block\",\n" +
                "  \"pools\": [\n" +
                "    {\n" +
                "      \"rolls\": 1,\n" +
                "      \"entries\": [\n" +
                "        {\n" +
                "          \"type\": \"minecraft:item\",\n" +
                "          \"name\": \""+resourceLocation.toString()+"\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"conditions\": [\n" +
                "        {\n" +
                "          \"condition\": \"minecraft:survives_explosion\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}");
    }

    public final void registerSlabDrop(Block var1) {
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(var1);
        this.injectResource("data/"+resourceLocation.getNamespace()+"/loot_tables/blocks/"+resourceLocation.getPath()+".json","{\n" +
                "  \"type\": \"minecraft:block\",\n" +
                "  \"pools\": [\n" +
                "    {\n" +
                "      \"rolls\": 1,\n" +
                "      \"entries\": [\n" +
                "        {\n" +
                "          \"type\": \"minecraft:item\",\n" +
                "          \"functions\": [\n" +
                "            {\n" +
                "              \"function\": \"minecraft:set_count\",\n" +
                "              \"conditions\": [\n" +
                "                {\n" +
                "                  \"condition\": \"minecraft:block_state_property\",\n" +
                "                  \"block\": \""+resourceLocation.toString()+"\",\n" +
                "                  \"properties\": {\n" +
                "                    \"type\": \"double\"\n" +
                "                  }\n" +
                "                }\n" +
                "              ],\n" +
                "              \"count\": 2\n" +
                "            },\n" +
                "            {\n" +
                "              \"function\": \"minecraft:explosion_decay\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"name\": \""+resourceLocation.toString()+"\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}");
    }

    public final void registerBlockModel(Block var1) {
        if (!Launch.isClient()) return;
        this.registerBlockModel(var1, Registry.BLOCK.getKey(var1));
    }

    public final void registerBlockModel(Block var1, ResourceLocation texture) {
        if (!Launch.isClient()) return;
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(var1);
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/blockstates/"+resourceLocation.getPath()+".json", "{\n" +
                "    \"variants\": {\n" +
                "        \"\": { \"model\": \""+resourceLocation.getNamespace()+":block/"+resourceLocation.getPath()+"\" }\n" +
                "    }\n" +
                "}\n");
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/models/block/"+resourceLocation.getPath()+".json", "{\n" +
                "    \"parent\": \"block/cube_all\",\n" +
                "    \"textures\": {\n" +
                "        \"all\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\"\n" +
                "    }\n" +
                "}\n");
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/models/item/"+resourceLocation.getPath()+".json", "{\n" +
                "    \"parent\": \""+resourceLocation.getNamespace()+":block/"+resourceLocation.getPath()+"\"\n" +
                "}\n");
    }

    public final void registerColumnModel(Block var1) {
        this.registerColumnModel(var1, new ResourceLocation(Registry.BLOCK.getKey(var1)+"_top"), Registry.BLOCK.getKey(var1));
    }

    public final void registerColumnModel(Block var1, ResourceLocation top, ResourceLocation side) {
        if (!Launch.isClient()) return;
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(var1);
        if (var1.defaultBlockState().hasProperty(BlockStateProperties.AXIS)) {
            this.injectResource("assets/"+resourceLocation.getNamespace()+"/blockstates/"+resourceLocation.getPath()+".json", "{\n" +
                    "    \"variants\": {\n" +
                    "        \"axis=y\": { \"model\": \""+resourceLocation.getNamespace()+":block/"+resourceLocation.getPath()+"\" },\n" +
                    "        \"axis=z\": { \"model\": \""+resourceLocation.getNamespace()+":block/"+resourceLocation.getPath()+"\", \"x\": 90 },\n" +
                    "        \"axis=x\": { \"model\": \""+resourceLocation.getNamespace()+":block/"+resourceLocation.getPath()+"\", \"x\": 90, \"y\": 90 }\n" +
                    "    }\n" +
                    "}\n");
        } else {
            this.injectResource("assets/"+resourceLocation.getNamespace()+"/blockstates/"+resourceLocation.getPath()+".json", "{\n" +
                    "    \"variants\": {\n" +
                    "        \"\": { \"model\": \""+resourceLocation.getNamespace()+":block/"+resourceLocation.getPath()+"\" }\n" +
                    "    }\n" +
                    "}\n");
        }
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/models/block/"+resourceLocation.getPath()+".json", "{\n" +
                "    \"parent\": \"block/cube_column\",\n" +
                "    \"textures\": {\n" +
                "        \"end\": \""+top.getNamespace()+":block/"+top.getPath()+"\",\n" +
                "        \"side\": \""+side.getNamespace()+":block/"+side.getPath()+"\"\n" +
                "    }\n" +
                "}\n");
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/models/item/"+resourceLocation.getPath()+".json", "{\n" +
                "    \"parent\": \""+resourceLocation.getNamespace()+":block/"+resourceLocation.getPath()+"\"\n" +
                "}\n");
    }

    public final void registerSlabModel(Block var1, Block from) {
        this.registerSlabModel(var1, Registry.BLOCK.getKey(from));
    }

    public final void registerSlabModel(Block var1, ResourceLocation from) {
        this.registerSlabModel(var1, from, from);
    }

    public final void registerSlabModel(Block var1, ResourceLocation fullBlock, ResourceLocation texture) {
        if (!Launch.isClient()) return;
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(var1);
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/blockstates/"+resourceLocation.getPath()+".json", "{\n" +
                "    \"variants\": {\n" +
                "        \"type=bottom\": { \"model\": \""+resourceLocation.toString()+"\" },\n" +
                "        \"type=top\": { \"model\": \""+resourceLocation.toString()+"_top\" },\n" +
                "        \"type=double\": { \"model\": \""+fullBlock.toString()+"\" }\n" +
                "    }" +
                "}\n");
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/models/block/"+resourceLocation.getPath()+".json", "{\n" +
                "    \"parent\": \"block/slab\",\n" +
                "    \"textures\": {\n" +
                "        \"bottom\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\",\n" +
                "        \"top\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\",\n" +
                "        \"side\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\"\n" +
                "    }\n" +
                "}\n");
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/models/block/"+resourceLocation.getPath()+"_top.json", "{\n" +
                "    \"parent\": \"block/slab_top\",\n" +
                "    \"textures\": {\n" +
                "        \"bottom\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\",\n" +
                "        \"top\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\",\n" +
                "        \"side\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\"\n" +
                "    }\n" +
                "}\n");
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/models/item/"+resourceLocation.getPath()+".json", "{\n" +
                "    \"parent\": \""+resourceLocation.getNamespace()+":block/"+resourceLocation.getPath()+"\"\n" +
                "}\n");
    }

    public final void registerStairModel(Block var1, Block texture) {
        this.registerStairModel(var1, Registry.BLOCK.getKey(texture));
    }

    public final void registerStairModel(Block var1, ResourceLocation texture) {
        if (!Launch.isClient()) return;
        ResourceLocation resourceLocation = Registry.BLOCK.getKey(var1);
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/blockstates/"+resourceLocation.getPath()+".json", "{\n" +
                "    \"variants\": {\n" +
                "        \"facing=east,half=bottom,shape=straight\":  { \"model\": \""+resourceLocation.toString()+"\" },\n" +
                "        \"facing=west,half=bottom,shape=straight\":  { \"model\": \""+resourceLocation.toString()+"\", \"y\": 180, \"uvlock\": true },\n" +
                "        \"facing=south,half=bottom,shape=straight\": { \"model\": \""+resourceLocation.toString()+"\", \"y\": 90, \"uvlock\": true },\n" +
                "        \"facing=north,half=bottom,shape=straight\": { \"model\": \""+resourceLocation.toString()+"\", \"y\": 270, \"uvlock\": true },\n" +
                "        \"facing=east,half=bottom,shape=outer_right\":  { \"model\": \""+resourceLocation.toString()+"_outer\" },\n" +
                "        \"facing=west,half=bottom,shape=outer_right\":  { \"model\": \""+resourceLocation.toString()+"_outer\", \"y\": 180, \"uvlock\": true },\n" +
                "        \"facing=south,half=bottom,shape=outer_right\": { \"model\": \""+resourceLocation.toString()+"_outer\", \"y\": 90, \"uvlock\": true },\n" +
                "        \"facing=north,half=bottom,shape=outer_right\": { \"model\": \""+resourceLocation.toString()+"_outer\", \"y\": 270, \"uvlock\": true },\n" +
                "        \"facing=east,half=bottom,shape=outer_left\":  { \"model\": \""+resourceLocation.toString()+"_outer\", \"y\": 270, \"uvlock\": true },\n" +
                "        \"facing=west,half=bottom,shape=outer_left\":  { \"model\": \""+resourceLocation.toString()+"_outer\", \"y\": 90, \"uvlock\": true },\n" +
                "        \"facing=south,half=bottom,shape=outer_left\": { \"model\": \""+resourceLocation.toString()+"_outer\" },\n" +
                "        \"facing=north,half=bottom,shape=outer_left\": { \"model\": \""+resourceLocation.toString()+"_outer\", \"y\": 180, \"uvlock\": true },\n" +
                "        \"facing=east,half=bottom,shape=inner_right\":  { \"model\": \""+resourceLocation.toString()+"_inner\" },\n" +
                "        \"facing=west,half=bottom,shape=inner_right\":  { \"model\": \""+resourceLocation.toString()+"_inner\", \"y\": 180, \"uvlock\": true },\n" +
                "        \"facing=south,half=bottom,shape=inner_right\": { \"model\": \""+resourceLocation.toString()+"_inner\", \"y\": 90, \"uvlock\": true },\n" +
                "        \"facing=north,half=bottom,shape=inner_right\": { \"model\": \""+resourceLocation.toString()+"_inner\", \"y\": 270, \"uvlock\": true },\n" +
                "        \"facing=east,half=bottom,shape=inner_left\":  { \"model\": \""+resourceLocation.toString()+"_inner\", \"y\": 270, \"uvlock\": true },\n" +
                "        \"facing=west,half=bottom,shape=inner_left\":  { \"model\": \""+resourceLocation.toString()+"_inner\", \"y\": 90, \"uvlock\": true },\n" +
                "        \"facing=south,half=bottom,shape=inner_left\": { \"model\": \""+resourceLocation.toString()+"_inner\" },\n" +
                "        \"facing=north,half=bottom,shape=inner_left\": { \"model\": \""+resourceLocation.toString()+"_inner\", \"y\": 180, \"uvlock\": true },\n" +
                "        \"facing=east,half=top,shape=straight\":  { \"model\": \""+resourceLocation.toString()+"\", \"x\": 180, \"uvlock\": true },\n" +
                "        \"facing=west,half=top,shape=straight\":  { \"model\": \""+resourceLocation.toString()+"\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
                "        \"facing=south,half=top,shape=straight\": { \"model\": \""+resourceLocation.toString()+"\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
                "        \"facing=north,half=top,shape=straight\": { \"model\": \""+resourceLocation.toString()+"\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" +
                "        \"facing=east,half=top,shape=outer_right\":  { \"model\": \""+resourceLocation.toString()+"_outer\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
                "        \"facing=west,half=top,shape=outer_right\":  { \"model\": \""+resourceLocation.toString()+"_outer\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" +
                "        \"facing=south,half=top,shape=outer_right\": { \"model\": \""+resourceLocation.toString()+"_outer\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
                "        \"facing=north,half=top,shape=outer_right\": { \"model\": \""+resourceLocation.toString()+"_outer\", \"x\": 180, \"uvlock\": true },\n" +
                "        \"facing=east,half=top,shape=outer_left\":  { \"model\": \""+resourceLocation.toString()+"_outer\", \"x\": 180, \"uvlock\": true },\n" +
                "        \"facing=west,half=top,shape=outer_left\":  { \"model\": \""+resourceLocation.toString()+"_outer\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
                "        \"facing=south,half=top,shape=outer_left\": { \"model\": \""+resourceLocation.toString()+"_outer\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
                "        \"facing=north,half=top,shape=outer_left\": { \"model\": \""+resourceLocation.toString()+"_outer\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" +
                "        \"facing=east,half=top,shape=inner_right\":  { \"model\": \""+resourceLocation.toString()+"_inner\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
                "        \"facing=west,half=top,shape=inner_right\":  { \"model\": \""+resourceLocation.toString()+"_inner\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" +
                "        \"facing=south,half=top,shape=inner_right\": { \"model\": \""+resourceLocation.toString()+"_inner\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
                "        \"facing=north,half=top,shape=inner_right\": { \"model\": \""+resourceLocation.toString()+"_inner\", \"x\": 180, \"uvlock\": true },\n" +
                "        \"facing=east,half=top,shape=inner_left\":  { \"model\": \""+resourceLocation.toString()+"_inner\", \"x\": 180, \"uvlock\": true },\n" +
                "        \"facing=west,half=top,shape=inner_left\":  { \"model\": \""+resourceLocation.toString()+"_inner\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
                "        \"facing=south,half=top,shape=inner_left\": { \"model\": \""+resourceLocation.toString()+"_inner\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
                "        \"facing=north,half=top,shape=inner_left\": { \"model\": \""+resourceLocation.toString()+"_inner\", \"x\": 180, \"y\": 270, \"uvlock\": true }\n" +
                "    }\n" +
                "}\n\n");
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/models/block/"+resourceLocation.getPath()+".json", "{\n" +
                "    \"parent\": \"block/stairs\",\n" +
                "    \"textures\": {\n" +
                "        \"bottom\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\",\n" +
                "        \"top\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\",\n" +
                "        \"side\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\"\n" +
                "    }\n" +
                "}\n");
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/models/block/"+resourceLocation.getPath()+"_inner.json", "{\n" +
                "    \"parent\": \"block/inner_stairs\",\n" +
                "    \"textures\": {\n" +
                "        \"bottom\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\",\n" +
                "        \"top\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\",\n" +
                "        \"side\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\"\n" +
                "    }\n" +
                "}\n");
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/models/block/"+resourceLocation.getPath()+"_outer.json", "{\n" +
                "    \"parent\": \"block/outer_stairs\",\n" +
                "    \"textures\": {\n" +
                "        \"bottom\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\",\n" +
                "        \"top\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\",\n" +
                "        \"side\": \""+texture.getNamespace()+":block/"+texture.getPath()+"\"\n" +
                "    }\n" +
                "}\n");
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/models/item/"+resourceLocation.getPath()+".json", "{\n" +
                "    \"parent\": \""+resourceLocation.getNamespace()+":block/"+resourceLocation.getPath()+"\"\n" +
                "}\n");
    }

    public final void registerItemModel(Item var1) {
        if (!Launch.isClient()) return;
        this.registerItemModel(var1, Registry.ITEM.getKey(var1));
    }

    public final void registerItemModel(Item var1, ResourceLocation texture) {
        if (!Launch.isClient()) return;
        ResourceLocation resourceLocation = Registry.ITEM.getKey(var1);
        this.injectResource("assets/"+resourceLocation.getNamespace()+"/models/item/"+resourceLocation.getPath()+".json", "{\n" +
                "    \"parent\": \"item/generated\",\n" +
                "    \"textures\": {\n" +
                "        \"layer0\": \""+texture.getNamespace()+":item/"+texture.getPath()+"\"\n" +
                "    }\n" +
                "}");
    }

    public final void registerShapedRecipe(String[] pattern, ItemLike result, Object... ingredients) {
        registerShapedRecipe(pattern, new ItemStack(result.asItem()), ingredients);
    }

    public final void registerShapedRecipe(String[] pattern, ItemStack result, Object... ingredients) {
        if (pattern.length <= 0 || pattern.length >= 4) {
            throw new IllegalArgumentException("Invalid pattern length!");
        }
        if (result == null) {
            throw new NullPointerException("Result can't be null;");
        }
        if (ingredients.length == 0 || (ingredients.length&1) != 0) {
            throw new IllegalArgumentException("Invalid ingredients length!");
        }
        StringBuilder recipeBuilder = new StringBuilder("{\n" +
                "  \"type\": \"minecraft:crafting_shaped\",\n" +
                "  \"pattern\": [");
        int i = 0;
        while (true) {
            recipeBuilder.append("\"").append(pattern[i]).append("\"");
            i++;
            if (i >= pattern.length) {
                break;
            }
            recipeBuilder.append(',');
        }
        recipeBuilder.append("],\n  \"key\": {");
        i = 0;
        while (true) {
            recipeBuilder.append("\"")
                    .append(ingredients[i].toString())
                    .append("\": { \"item\": \"");
            if (ingredients[i+1] instanceof Item) {
                ingredients[i+1] = Registry.ITEM.getKey((Item) ingredients[i+1]);
            } else if (ingredients[i+1] instanceof Block) {
                ingredients[i+1] = Registry.BLOCK.getKey((Block) ingredients[i+1]);
            }
            recipeBuilder.append(ingredients[i+1].toString())
                    .append("\"\n    }");
            i+=2;
            if (i >= ingredients.length) {
                break;
            }
            recipeBuilder.append(',');
        }
        recipeBuilder.append("},\n" +
                "  \"result\": {\n" +
                "    \"item\": \"")
                .append(Registry.ITEM.getKey(result.getItem()).toString())
                .append("\",\n    \"count\": ")
                .append(result.getCount())
                .append("\n  }\n}");
        this.injectResource("data/"+this.id+"/recipes/"+Registry.ITEM.getKey(result.getItem()).getPath()+"_"+recipeID+++".json", recipeBuilder.toString());
    }

    public final void registerFurnaceRecipe(ItemLike from,ItemLike to) {
        this.registerFurnaceRecipe(from, to, 0F);
    }

    public final void registerFurnaceRecipe(ItemLike from,ItemLike to,float xp) {
        this.registerFurnaceRecipe(from, to, xp, 200);
    }

    public final void registerFurnaceRecipe(ItemLike from, ItemLike to, int cookingTime) {
        this.registerFurnaceRecipe(from, to, 0, cookingTime);
    }

    public final void registerFurnaceRecipe(ItemLike from,ItemLike to,float xp,int cookingTime) {
        this.registerFurnaceRecipe(from.asItem(), to.asItem(), xp, cookingTime);
    }

    public final void registerFurnaceRecipe(Item from,Item to,float xp,int cookingTime) {
        this.injectResource("data/"+this.id+"/recipes/"+Registry.ITEM.getKey(to).getPath()+"_"+recipeID+++".json", "{\n" +
                "  \"type\": \"minecraft:smelting\",\n" +
                "  \"ingredient\": {\n" +
                "    \"item\": \""+Registry.ITEM.getKey(from).toString()+"\"\n" +
                "  },\n" +
                "  \"result\": \""+Registry.ITEM.getKey(to).toString()+"\",\n" +
                "  \"experience\": "+xp+",\n" +
                "  \"cookingtime\": "+cookingTime+"\n" +
                "}");
    }

    public final int nextRecipeID() {
        return recipeID++;
    }
}
