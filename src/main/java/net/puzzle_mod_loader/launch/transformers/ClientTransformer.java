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
                    final boolean serverHook = name.equals("setCurrentServer");
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
                            } else if (serverHook && opcode == RETURN) {
                                super.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/ServerHelper", "updateBlacklistStatus", "()V", false);
                            }
                            super.visitInsn(opcode);
                        }
                    };
                }
            }, 0);
            return classWriter.toByteArray();
        }
        if (className.equals("net.minecraft.client.ClientBrandRetriever")) {
            ClassReader classReader = new ClassReader(bytes);
            ClassWriter classWriter = new ClassWriter(0);
            classReader.accept(new ClassVisitor(ASM7, classWriter) {
                @Override
                public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                    super.visit(version, access, name, signature, superName, interfaces);
                    super.visitField(ACC_PRIVATE|ACC_STATIC|ACC_SYNTHETIC, "stack", "[Z", null, null);
                }

                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    if (name.equals("getClientModName")) {
                        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                        methodVisitor.visitFieldInsn(GETSTATIC, "net/minecraft/client/ClientBrandRetriever", "stack", "[Z");
                        methodVisitor.visitInsn(DUP);//2
                        Label l1 = new Label();
                        methodVisitor.visitJumpInsn(IFNONNULL, l1);//1
                        methodVisitor.visitInsn(POP);//0
                        methodVisitor.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/ServerHelper", "updateBlacklistStatus", "()V", false);
                        methodVisitor.visitInsn(ICONST_1);
                        methodVisitor.visitIntInsn(NEWARRAY, T_BOOLEAN);
                        methodVisitor.visitInsn(DUP);
                        methodVisitor.visitInsn(DUP);
                        methodVisitor.visitFieldInsn(PUTSTATIC, "net/minecraft/client/ClientBrandRetriever", "stack", "[Z");
                        methodVisitor.visitLdcInsn(0);
                        methodVisitor.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/ServerHelper", "isOnBlacklistedServer", "()Z", false);
                        methodVisitor.visitInsn(BASTORE);
                        methodVisitor.visitLabel(l1);
                        methodVisitor.visitInsn(ICONST_0);
                        methodVisitor.visitInsn(BALOAD);
                        Label l2 = new Label();
                        methodVisitor.visitJumpInsn(IFNE, l2);
                        methodVisitor.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/ServerHelper", "displayBrand", "()Ljava/lang/String;", false);
                        methodVisitor.visitInsn(ARETURN);
                        methodVisitor.visitLabel(l2);
                        methodVisitor.visitLdcInsn("Vanilla");
                        methodVisitor.visitInsn(ARETURN);
                        methodVisitor.visitMaxs(4, 0);
                        return new MethodVisitor(ASM7) {};
                    }
                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
            }, 0);
            return classWriter.toByteArray();
        }
        return bytes;
    }
}
