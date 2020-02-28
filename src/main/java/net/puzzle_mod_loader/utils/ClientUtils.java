package net.puzzle_mod_loader.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import net.puzzle_mod_loader.compact.ClientOnly;

@ClientOnly
public class ClientUtils {
    public static final Minecraft mc = Minecraft.getInstance();

    public static LocalPlayer player() {
        return mc.player;
    }

    public static Level world() {
        return mc.level;
    }
}
