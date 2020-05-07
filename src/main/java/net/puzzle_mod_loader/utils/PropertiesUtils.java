package net.puzzle_mod_loader.utils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class PropertiesUtils {
    public static Block.Properties sound(Block.Properties properties, SoundType soundType) {
        return properties.sound(soundType);
    }

    public static Block.Properties lightLevel(Block.Properties properties, int lightEmission) {
        return properties.lightLevel(lightEmission);
    }

    public static Block.Properties jumpFactor(Block.Properties properties, float jumpFactor) {
        return properties.jumpFactor(jumpFactor);
    }

    public static Block.Properties randomTicks(Block.Properties properties) {
        properties.isTicking = true;
        return properties;
    }
}
