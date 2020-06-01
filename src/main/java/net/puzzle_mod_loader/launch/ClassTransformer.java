package net.puzzle_mod_loader.launch;

import org.objectweb.asm.Opcodes;

public interface ClassTransformer extends Opcodes {
    int ASM_BUILD = ASM8;

    byte[] transform(byte[] bytes,String className);
}
