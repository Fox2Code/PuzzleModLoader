package net.puzzle_mod_loader.utils;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

public class PropertiesUtils {
    public static Block.Properties sound(Block.Properties properties, SoundType soundType) {
        try {
            ReflectedClass.of(properties).set("sound", soundType);
        } catch (Exception ignored) {}
        return properties;
    }

    public static Block.Properties lightLevel(Block.Properties properties, int lightEmission) {
        try {
            ReflectedClass.of(properties).set("lightEmission", lightEmission);
        } catch (Exception ignored) {}
        return properties;
    }

    public static Block.Properties randomTicks(Block.Properties properties) {
        try {
            ReflectedClass.of(properties).set("isTicking", true);
        } catch (Exception ignored) {}
        return properties;
    }
}
