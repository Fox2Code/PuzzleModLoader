package net.puzzle_mod_loader.core;

import com.fox2code.udk.startup.Internal;
import org.spongepowered.asm.mixin.Mixins;

public class CoreInit {
    @Internal
    public static void init() {
        Mixins.addConfiguration("mixins.puzzle.json");
        ModLoader.loadMods();
    }
}
