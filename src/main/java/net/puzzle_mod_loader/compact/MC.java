package net.puzzle_mod_loader.compact;

import net.minecraft.SharedConstants;

public final class MC {
    public static final int V1_14_4 = 498;
    public static final int V1_15 = 573;
    public static final int V1_15_1 = 575;
    public static final int V1_15_2 = 578;
    // TODO Minecraft-less implementation
    public static final boolean SNAPSHOT = !SharedConstants.getCurrentVersion().isStable();
    public static final int CURRENT = SharedConstants.getCurrentVersion().getProtocolVersion();
    public static final int CURRENT_WORLD = SharedConstants.getCurrentVersion().getWorldVersion();
    public static final int CURRENT_PACK_REV = SharedConstants.getCurrentVersion().getPackVersion();
}
