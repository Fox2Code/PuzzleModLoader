package net.puzzle_mod_loader.registry;

import net.minecraft.world.level.ItemLike;

public interface ItemProvider<T extends CompactEntry> {
    ItemLike provide(T compact,String id);
}
