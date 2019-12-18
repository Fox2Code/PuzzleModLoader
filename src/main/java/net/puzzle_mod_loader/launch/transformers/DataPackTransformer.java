package net.puzzle_mod_loader.launch.transformers;

import net.puzzle_mod_loader.launch.ClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class DataPackTransformer implements ClassTransformer {
    @Override
    public byte[] transform(byte[] bytes, String className) {
        if (className.equals("net.minecraft.server.packs.repository.ServerPacksSource")/* ||
                className.equals("net.minecraft.client.resources.ClientPackSource")*/) {
            ClassReader classReader = new ClassReader(bytes);
            ClassWriter classWriter = new ClassWriter(0);
            classReader.accept(new ClassVisitor(ASM7, classWriter) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    if (name.equals("loadPacks") && descriptor.equals("(Ljava/util/Map;Lnet/minecraft/server/packs/repository/UnopenedPack$UnopenedPackConstructor;)V")) {
                        return new MethodVisitor(ASM7, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == RETURN) {
                                    super.visitVarInsn(ALOAD, 1);
                                    super.visitVarInsn(ALOAD, 2);
                                    super.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/Hooks", "dataPackHook", "(Ljava/util/Map;Lnet/minecraft/server/packs/repository/UnopenedPack$UnopenedPackConstructor;)V", false);
                                }
                                super.visitInsn(opcode);
                            }
                        };
                    }
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
            }, 0);
            return classWriter.toByteArray();
        }
        return bytes;
    }
}
