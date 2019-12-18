package net.puzzle_mod_loader.launch.transformers;

import net.puzzle_mod_loader.launch.ClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

public class ServerTransformer implements ClassTransformer {
    @Override
    public byte[] transform(byte[] bytes, String className) {
        if (className.equals("net.minecraft.server.MinecraftServer")) {
            ClassReader classReader = new ClassReader(bytes);
            ClassWriter classWriter = new ClassWriter(0);
            classReader.accept(new ClassVisitor(ASM7, classWriter) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    if (name.equals("run") && descriptor.equals("()V")) {
                        return new MethodVisitor(ASM7, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                            boolean init, tick;

                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                                if (opcode == INVOKESTATIC && !init) {
                                    if (owner.equals("net/minecraft/Util") && name.equals("getMillis")) {
                                        super.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/Hooks", "onServerInit", "(Lnet/minecraft/server/MinecraftServer;)V", false);
                                        super.visitVarInsn(ALOAD, 0);
                                        this.init = true;
                                    }
                                }
                                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                                if (opcode == INVOKEVIRTUAL && !tick) {
                                    if (owner.equals("net/minecraft/server/MinecraftServer") && name.equals("tickServer")) {
                                        super.visitVarInsn(ALOAD, 0);
                                        super.visitVarInsn(ALOAD, 0);
                                        super.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/server/MinecraftServer", "haveTime", "()Z", false);
                                        super.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/Hooks", "onServerTick", "(Lnet/minecraft/server/MinecraftServer;Z)V", false);
                                    }
                                }
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
