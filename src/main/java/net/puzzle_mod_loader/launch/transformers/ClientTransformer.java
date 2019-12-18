package net.puzzle_mod_loader.launch.transformers;

import net.puzzle_mod_loader.launch.ClassTransformer;
import org.objectweb.asm.*;

public class ClientTransformer implements ClassTransformer {
    @Override
    public byte[] transform(byte[] bytes, String className) {
        if (className.equals("net.minecraft.client.Minecraft")) {
            ClassReader classReader = new ClassReader(bytes);
            ClassWriter classWriter = new ClassWriter(0);
            classReader.accept(new ClassVisitor(ASM7, classWriter) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    final boolean screenHook = name.equals("setScreen") && descriptor.equals("(Lnet/minecraft/client/gui/screens/Screen;)V");
                    final boolean packHook = name.equals("reloadResourcePacks") || name.equals("<init>");
                    final boolean tickHook = name.equals("tick");
                    return new MethodVisitor(ASM7, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                        @Override
                        public void visitCode() {
                            super.visitCode();
                            if (screenHook) {
                                super.visitVarInsn(ALOAD, 0);
                                super.visitFieldInsn(GETFIELD, "net/minecraft/client/Minecraft", "screen", "Lnet/minecraft/client/gui/screens/Screen;");
                                super.visitVarInsn(ALOAD, 1);
                                super.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/ClientHooks", "clientChangeGui", "(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/screens/Screen;)Lnet/puzzle_mod_loader/events/gui/ChangeGuiEvent;", false);
                                super.visitInsn(DUP);
                                super.visitMethodInsn(INVOKEVIRTUAL, "net/puzzle_mod_loader/events/gui/ChangeGuiEvent", "isCanceled", "()Z", false);
                                Label label = new Label();
                                super.visitJumpInsn(IFEQ, label);
                                super.visitInsn(POP);
                                super.visitInsn(RETURN);
                                super.visitLabel(label);
                                super.visitMethodInsn(INVOKEVIRTUAL, "net/puzzle_mod_loader/events/gui/ChangeGuiEvent", "getTo", "()Lnet/minecraft/client/gui/screens/Screen;", false);
                                super.visitVarInsn(ASTORE, 1);
                            }
                        }

                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                            if (opcode == INVOKEVIRTUAL && owner.equals("net/minecraft/client/gui/screens/Screen")) {
                                switch (name) {
                                    default:
                                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                                        break;
                                    case "init":
                                        super.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/ClientHooks", "clientGuiInit", "(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/Minecraft;II)V", false);
                                        break;
                                    case "resize":
                                        super.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/ClientHooks", "clientGuiResize", "(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/Minecraft;II)V", false);
                                        break;
                                }
                                } else {
                                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                            }
                        }

                        @Override
                        public void visitTypeInsn(int opcode, String type) {
                            super.visitTypeInsn(opcode, type);
                            if (packHook && opcode == CHECKCAST && type.equals("java/util/List")) {
                                super.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/ClientHooks", "hookPacks", "(Ljava/util/List;)Ljava/util/List;", false);
                            }
                        }

                        @Override
                        public void visitInsn(int opcode) {
                            if (tickHook && opcode == RETURN) {
                                super.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/ClientHooks", "clientTick", "()V", false);
                            }
                            super.visitInsn(opcode);
                        }
                    };
                }
            }, 0);
            return classWriter.toByteArray();
        }
        return bytes;
    }
}
