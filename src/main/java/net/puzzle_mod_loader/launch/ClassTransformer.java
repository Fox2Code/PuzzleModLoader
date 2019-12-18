package net.puzzle_mod_loader.launch;

import org.objectweb.asm.Opcodes;

public interface ClassTransformer extends Opcodes {
    byte[] transform(byte[] bytes,String className);

}
