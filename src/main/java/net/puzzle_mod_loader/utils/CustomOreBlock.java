package net.puzzle_mod_loader.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.OreBlock;

import java.util.Random;

public class CustomOreBlock extends OreBlock {
    private final int minXP ,maxXP;

    public CustomOreBlock(Properties properties,int minXP,int maxXP) {
        super(properties);
        this.minXP = minXP;
        this.maxXP = maxXP;
    }

    @Override
    public int xpOnDrop(Random var1) {
        return Mth.nextInt(var1, minXP, maxXP);
    }
}
