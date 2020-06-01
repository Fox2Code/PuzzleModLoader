package net.puzzle_mod_loader.core.mixin;

import net.puzzle_mod_loader.launch.Launch;
import org.spongepowered.asm.launch.platform.IMixinPlatformServiceAgent;
import org.spongepowered.asm.launch.platform.MixinPlatformAgentAbstract;
import org.spongepowered.asm.launch.platform.MixinPlatformManager;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.util.Constants;

import java.util.Collection;

/**
 * This class only exist to tell to Mixins on which side we are
 */
public class MixinPlatformAgent extends MixinPlatformAgentAbstract implements IMixinPlatformServiceAgent {

    @Override
    public AcceptResult accept(MixinPlatformManager manager, IContainerHandle handle) {
        return AcceptResult.REJECTED;
    }

    @Override
    public void init() {

    }

    @Override
    public String getSideName() {
        return Launch.isClient() ? Constants.SIDE_CLIENT : Constants.SIDE_SERVER;
    }

    @Override
    public Collection<IContainerHandle> getMixinContainers() {
        return null;
    }
}
