package net.puzzle_mod_loader.registry;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.puzzle_mod_loader.compact.MC;

import java.awt.*;

public class SRegistry {
    public static void woodUpdate() {
        if (MC.CURRENT >= 701) {//701 = 20w06a
            WoodRegistry.woodHashMap.put("crimson", new Wood("crimson", WoodType.CRIMSON, Blocks.CRIMSON_STEM, Blocks.CRIMSON_PLANKS, Blocks.NETHER_WART_BLOCK,
                    Blocks.CRIMSON_FUNGI, Blocks.STRIPPED_CRIMSON_STEM, new Color(126, 58, 86))
                    .addBlock("fence", Blocks.CRIMSON_FENCE).addBlock("gate", Blocks.CRIMSON_FENCE_GATE)
                    .addBlock("slab", Blocks.CRIMSON_SLAB).addBlock("stairs", Blocks.CRIMSON_STAIRS)
                    .addBlock("button", Blocks.CRIMSON_BUTTON)
            );
            WoodRegistry.woodHashMap.put("warped", new Wood("warped", WoodType.WARPED, Blocks.WARPED_STEM, Blocks.WARPED_PLANKS, Blocks.WARPED_WART_BLOCK,
                    Blocks.WARPED_FUNGI, Blocks.STRIPPED_WARPED_STEM, new Color(57, 131, 130))
                    .addBlock("fence", Blocks.WARPED_FENCE).addBlock("gate", Blocks.WARPED_FENCE_GATE)
                    .addBlock("slab", Blocks.WARPED_SLAB).addBlock("stairs", Blocks.WARPED_STAIRS)
                    .addBlock("button", Blocks.WARPED_BUTTON)
            );
        }
    }
}
