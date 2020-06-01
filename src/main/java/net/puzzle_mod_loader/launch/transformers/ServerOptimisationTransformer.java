package net.puzzle_mod_loader.launch.transformers;

import net.puzzle_mod_loader.launch.ClassTransformer;

public class ServerOptimisationTransformer implements ClassTransformer {
    @Override
    public byte[] transform(byte[] bytes, String className) {
        return bytes;
    }
}
