package net.puzzle_mod_loader.registry;

import com.fox2code.udk.build.Internal;
import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.puzzle_mod_loader.core.ModLoader;
import net.puzzle_mod_loader.utils.ReflectedClass;

import java.awt.*;
import java.util.*;
import java.util.List;

public class WoodRegistry {
    static HashMap<String, Wood> woodHashMap;
    static HashMap<String, List<ItemProvider<Wood>>> providers;

    static {
        woodHashMap = new HashMap<>();
        woodHashMap.put("oak", new Wood("oak", WoodType.OAK, Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_LEAVES,
                Blocks.OAK_SAPLING, Blocks.STRIPPED_OAK_LOG, new Color(184, 148, 95))
                .addBlock("fence", Blocks.OAK_FENCE).addBlock("gate", Blocks.OAK_FENCE_GATE)
                .addBlock("slab", Blocks.OAK_SLAB).addBlock("stairs", Blocks.OAK_STAIRS)
                .addBlock("button", Blocks.OAK_BUTTON)
        );
        woodHashMap.put("birch", new Wood("birch", WoodType.BIRCH, Blocks.BIRCH_LOG, Blocks.BIRCH_PLANKS, Blocks.BIRCH_LEAVES,
                Blocks.BIRCH_SAPLING, Blocks.STRIPPED_BIRCH_LOG, new Color(215, 193, 133))
                .addBlock("fence", Blocks.BIRCH_FENCE).addBlock("gate", Blocks.BIRCH_FENCE_GATE)
                .addBlock("slab", Blocks.BIRCH_SLAB).addBlock("stairs", Blocks.BIRCH_STAIRS)
                .addBlock("button", Blocks.BIRCH_BUTTON)
        );
        woodHashMap.put("spruce", new Wood("spruce", WoodType.SPRUCE, Blocks.SPRUCE_LOG, Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LEAVES,
                Blocks.SPRUCE_SAPLING, Blocks.STRIPPED_SPRUCE_LOG, new Color(130, 97, 58))
                .addBlock("fence", Blocks.SPRUCE_FENCE).addBlock("gate", Blocks.SPRUCE_FENCE_GATE)
                .addBlock("slab", Blocks.SPRUCE_SLAB).addBlock("stairs", Blocks.SPRUCE_STAIRS)
                .addBlock("button", Blocks.SPRUCE_BUTTON)
        );
        woodHashMap.put("jungle", new Wood("jungle", WoodType.JUNGLE, Blocks.JUNGLE_LOG, Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LEAVES,
                Blocks.JUNGLE_SAPLING, Blocks.STRIPPED_JUNGLE_LOG, new Color(184, 135, 100))
                .addBlock("fence", Blocks.JUNGLE_FENCE).addBlock("gate", Blocks.JUNGLE_FENCE_GATE)
                .addBlock("slab", Blocks.JUNGLE_SLAB).addBlock("stairs", Blocks.JUNGLE_STAIRS)
                .addBlock("button", Blocks.JUNGLE_BUTTON)
        );
        woodHashMap.put("dark_oak", new Wood("dark_oak", WoodType.DARK_OAK, Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LEAVES,
                Blocks.DARK_OAK_SAPLING, Blocks.STRIPPED_DARK_OAK_LOG, new Color(79, 50, 24))
                .addBlock("fence", Blocks.DARK_OAK_FENCE).addBlock("gate", Blocks.DARK_OAK_FENCE_GATE)
                .addBlock("slab", Blocks.DARK_OAK_SLAB).addBlock("stairs", Blocks.DARK_OAK_STAIRS)
                .addBlock("button", Blocks.DARK_OAK_BUTTON)
        );
        woodHashMap.put("acacia", new Wood("acacia", WoodType.ACACIA, Blocks.ACACIA_LOG, Blocks.ACACIA_PLANKS, Blocks.ACACIA_LEAVES,
                Blocks.ACACIA_SAPLING, Blocks.STRIPPED_ACACIA_LOG, new Color(186, 99, 55))
                .addBlock("fence", Blocks.ACACIA_FENCE).addBlock("gate", Blocks.ACACIA_FENCE_GATE)
                .addBlock("slab", Blocks.ACACIA_SLAB).addBlock("stairs", Blocks.ACACIA_STAIRS)
                .addBlock("button", Blocks.ACACIA_BUTTON)
        );
        WoodRegistry.woodHashMap.put("crimson", new Wood("crimson", WoodType.CRIMSON, Blocks.CRIMSON_STEM, Blocks.CRIMSON_PLANKS, Blocks.NETHER_WART_BLOCK,
                Blocks.CRIMSON_FUNGUS, Blocks.STRIPPED_CRIMSON_STEM, new Color(126, 58, 86))
                .addBlock("fence", Blocks.CRIMSON_FENCE).addBlock("gate", Blocks.CRIMSON_FENCE_GATE)
                .addBlock("slab", Blocks.CRIMSON_SLAB).addBlock("stairs", Blocks.CRIMSON_STAIRS)
                .addBlock("button", Blocks.CRIMSON_BUTTON)
        );
        WoodRegistry.woodHashMap.put("warped", new Wood("warped", WoodType.WARPED, Blocks.WARPED_STEM, Blocks.WARPED_PLANKS, Blocks.WARPED_WART_BLOCK,
                Blocks.WARPED_FUNGUS, Blocks.STRIPPED_WARPED_STEM, new Color(57, 131, 130))
                .addBlock("fence", Blocks.WARPED_FENCE).addBlock("gate", Blocks.WARPED_FENCE_GATE)
                .addBlock("slab", Blocks.WARPED_SLAB).addBlock("stairs", Blocks.WARPED_STAIRS)
                .addBlock("button", Blocks.WARPED_BUTTON)
        );
        providers = new HashMap<>();
    }

    public static Set<Wood> getWoods() {
        return ImmutableSet.copyOf(woodHashMap.values());
    }

    public static Wood byName(String name) {
        return woodHashMap.get(name);
    }

    public static Wood byType(WoodType type) {
        return woodHashMap.get(type.name());
    }

    public static void addProvider(String id,ItemProvider<Wood> provider) {
        if (ModLoader.isInitDone()) throw new IllegalStateException("Can't register providers after initialisation!");
        providers.computeIfAbsent(id, ($) -> new ArrayList<>()).add(provider);
    }

    @Internal
    public static void parseProviders() {
        for (Wood wood:woodHashMap.values()) {
            for (Map.Entry<String, List<ItemProvider<Wood>>> entry:providers.entrySet()) {
                if (wood.hasItem(entry.getKey())) {
                    continue;
                }
                for (ItemProvider<Wood> itemProvider:entry.getValue()) {
                    ItemLike itemLike = itemProvider.provide(wood, entry.getKey());
                    if (itemLike == null) {
                        continue;
                    }
                    if (itemLike instanceof Block) {
                        wood.addBlock(entry.getKey(), (Block) itemLike);
                    } else {
                        wood.addItem(entry.getKey(), itemLike.asItem());
                    }
                }
            }
        }
    }
}
