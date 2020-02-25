package net.puzzle_mod_loader.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.puzzle_mod_loader.core.ModLoader;
import net.puzzle_mod_loader.utils.ReflectedClass;

import java.awt.*;
import java.util.HashMap;

public class Wood implements CompactEntry {
    private static ReflectedClass WoodType = ReflectedClass.of(WoodType.class);

    private static WoodType createWoodType(String type) {
        if (ModLoader.isInitDone()) throw new IllegalStateException("Can't register providers after initialisation!");
        if (WoodRegistry.woodHashMap.containsKey(type)) {
            throw new IllegalArgumentException("Duplicate ID");
        }
        WoodType woodType;
        try {
            woodType = (WoodType) WoodType.newInstance0(type);
            WoodType.run0("register", woodType);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return woodType;
    }

    private static boolean builtIn(String id) {
        switch (id) {
            default:
                return false;
            case "log":
            case "plank":
            case "sapling":
            case "striped":
            case "leaves":
                return true;
        }
    }

    private final WoodType woodType;
    private final Color accent;
    private final Block log, plank, sapling, striped, leaves;
    public final ResourceLocation logTop, logSide, logTopStriped, logSideStriped, plankTexture, leavesTexture;

    private HashMap<String, Block> blocks = new HashMap<>();
    private HashMap<String, Item> items = new HashMap<>();

    public Wood(String woodType,Block log, Block plank, Block leaves, Block sapling, Block striped, Color accent) {
        this(woodType, log, plank, leaves, sapling, striped, accent, new ResourceLocation(Registry.BLOCK.getKey(log).toString()+"_top"), Registry.BLOCK.getKey(striped), new ResourceLocation(Registry.BLOCK.getKey(log).toString()+"_top"), Registry.BLOCK.getKey(striped), Registry.BLOCK.getKey(plank), Registry.BLOCK.getKey(leaves));
    }

    public Wood(String woodType,Block log, Block plank, Block leaves, Block sapling, Block striped, Color accent, ResourceLocation logTop, ResourceLocation logSide, ResourceLocation logTopStriped, ResourceLocation logSideStriped, ResourceLocation plankTex,ResourceLocation leavesTexture) {
        this(woodType, createWoodType(woodType), log, plank, leaves, sapling, striped, accent, logTop, logSide, logTopStriped, logSideStriped, plankTex, leavesTexture);
        WoodRegistry.woodHashMap.put(woodType, this);
    }

    Wood(String id,WoodType woodType,Block log, Block plank, Block leaves, Block sapling, Block striped, Color accent) {
        this(id, woodType, log, plank, leaves, sapling, striped, accent, new ResourceLocation(Registry.BLOCK.getKey(log).toString()+"_top"), Registry.BLOCK.getKey(striped), new ResourceLocation(Registry.BLOCK.getKey(log).toString()+"_top"), Registry.BLOCK.getKey(striped), Registry.BLOCK.getKey(plank), Registry.BLOCK.getKey(leaves));
    }

    Wood(String id,WoodType woodType,Block log, Block plank, Block leaves, Block sapling, Block striped, Color accent, ResourceLocation logTop, ResourceLocation logSide, ResourceLocation logTopStriped, ResourceLocation logSideStriped, ResourceLocation plankTexture, ResourceLocation leavesTexture) {
        if (!woodType.name().equals(id)) {
            throw new IllegalArgumentException("ID Mismatch");
        }
        this.woodType = woodType;
        this.accent = accent;
        this.log = log;
        this.plank = plank;
        this.sapling = sapling;
        this.striped = striped;
        this.leaves = leaves;
        this.logTop = logTop;
        this.logSide = logSide;
        this.logTopStriped = logTopStriped;
        this.logSideStriped = logSideStriped;
        this.plankTexture = plankTexture;
        this.leavesTexture = leavesTexture;
    }

    public Wood addBlock(String id,Block block) {
        if (builtIn(id)) {
            throw new IllegalArgumentException("Can't register a builtin ID!");
        }
        blocks.put(id, block);
        return this;
    }

    public Wood addItem(String id,Item item) {
        if (builtIn(id)) {
            throw new IllegalArgumentException("Can't register a builtin ID!");
        }
        items.put(id, item);
        return this;
    }

    public boolean hasItem(String id) {
        return builtIn(id) || blocks.containsKey(id) || items.containsKey(id);
    }

    public boolean hasBlock(String id) {
        return builtIn(id) || blocks.containsKey(id);
    }

    public Block getBlock(String key) {
        switch (key) {
            default:
                return blocks.get(key);
            case "log":
                return log;
            case "plank":
                return plank;
            case "sapling":
                return sapling;
            case "striped":
                return striped;
            case "leaves":
                return leaves;
        }
    }

    public Item getItem(String key) {
        switch (key) {
            default:
                Block b = blocks.get(key);
                if (b !=null) {
                    return b.asItem();
                }
                return items.get(key);
            case "log":
                return log.asItem();
            case "plank":
                return plank.asItem();
            case "sapling":
                return sapling.asItem();
            case "striped":
                return striped.asItem();
            case "leaves":
                return leaves.asItem();
        }
    }

    public WoodType getWoodType() {
        return woodType;
    }

    public Color getAccent() {
        return accent;
    }

    public Block getLog() {
        return log;
    }

    public Block getPlank() {
        return plank;
    }

    public Block getSapling() {
        return sapling;
    }

    public Block getStriped() {
        return striped;
    }

    public Block getLeaves() {
        return leaves;
    }
}
