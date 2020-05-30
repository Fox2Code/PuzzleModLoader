package net.puzzle_mod_loader.core;

import com.fox2code.udk.build.Internal;
import net.puzzle_mod_loader.launch.Launch;
import net.puzzle_mod_loader.utils.ReflectedClass;
import org.spongepowered.asm.mixin.Mixins;

public class CoreInit {
    @Internal
    public static void init() {
        Mixins.addConfiguration("mixins.puzzle.json");
        if (Launch.getClassLoader().getResource("mixins.puzzle.snapshot.json") != null) {
            Mixins.addConfiguration("mixins.puzzle.snapshot.json");
        }
        ModLoader.loadMods();
        try {
            ReflectedClass.$(CoreInit.class, "ModPart.init()");
        } catch (ReflectiveOperationException ignored) {}
    }
}
