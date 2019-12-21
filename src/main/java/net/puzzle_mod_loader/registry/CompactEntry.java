package net.puzzle_mod_loader.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.awt.*;

public interface CompactEntry {
    Color getAccent();
    CompactEntry addBlock(String id, Block block);
    CompactEntry addItem(String id, Item item);
    boolean hasItem(String id);
    boolean hasBlock(String id);
    Block getBlock(String key);
    Item getItem(String key);
}
