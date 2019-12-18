package net.puzzle_mod_loader.core;

import org.spongepowered.asm.mixin.Mixins;

public class CoreInit {
    public static void init() {
        Mixins.addConfiguration("mixins.puzzle.json");
        ModLoader.loadMods();
    }
}
