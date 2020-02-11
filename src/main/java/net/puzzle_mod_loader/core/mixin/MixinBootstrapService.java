package net.puzzle_mod_loader.core.mixin;

import net.puzzle_mod_loader.launch.Launch;
import org.spongepowered.asm.service.IMixinServiceBootstrap;
import org.spongepowered.asm.service.ServiceInitialisationException;

public class MixinBootstrapService implements IMixinServiceBootstrap {
    @Override
    public String getName() {
        return "PuzzleMixinService";
    }

    @Override
    public String getServiceClassName() {
        return "net.puzzle_mod_loader.core.mixin.MixinService";
    }

    @Override
    public void bootstrap() {
        try {
            Launch.getClassLoader().hashCode();
        } catch (Throwable th) {
            throw new ServiceInitialisationException(this.getName() + " is not available");
        }
    }
}
