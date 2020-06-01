package net.puzzle_mod_loader.launch.transformers;

import net.puzzle_mod_loader.launch.ClassTransformer;
import org.objectweb.asm.*;

public class ConnectTransformer implements ClassTransformer {
    @Override
    public byte[] transform(byte[] bytes, String className) {
        if (className.equals("net.minecraft.client.multiplayer.ServerStatusPinger$1")) {
            ClassReader classReader = new ClassReader(bytes);
            ClassWriter classWriter = new ClassWriter(0);
            classReader.accept(new ClassVisitor(ASM_BUILD, classWriter) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    return new MethodVisitor(ASM_BUILD, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                            if (opcode == INVOKEVIRTUAL && owner.equals("net/minecraft/network/Connection") && name.equals("disconnect")) {
                                super.visitVarInsn(ALOAD, 0);
                                super.visitFieldInsn(GETFIELD, "net/minecraft/client/multiplayer/ServerStatusPinger$1", "val$data", "Lnet/minecraft/client/multiplayer/ServerData;");
                                super.visitVarInsn(ALOAD, 0);
                                super.visitFieldInsn(GETFIELD, "net/minecraft/client/multiplayer/ServerStatusPinger$1", "this$0", "Lnet/minecraft/client/multiplayer/ServerStatusPinger;");
                                super.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/ClientHooks", "pingHook", "(Lnet/minecraft/client/multiplayer/ServerData;Lnet/minecraft/client/multiplayer/ServerStatusPinger;)", false);
                            }
                        }
                    };
                }
            }, 0);
            return classWriter.toByteArray();
        }
        if (className.equals("net.minecraft.client.gui.screens.ConnectScreen$1")) {
            ClassReader classReader = new ClassReader(bytes);
            ClassWriter classWriter = new ClassWriter(0);
            classReader.accept(new ClassVisitor(ASM_BUILD, classWriter) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    return new MethodVisitor(ASM7, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                            if (opcode == INVOKESPECIAL && owner.equals("net/minecraft/network/protocol/handshake/ClientIntentionPacket") && name.equals("<init>")) {
                                super.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/ClientHooks", "preJoinHook", "(Lnet/minecraft/network/protocol/handshake/ClientIntentionPacket;)Lnet/minecraft/network/protocol/handshake/ClientIntentionPacket;", false);
                            }
                        }
                    };
                }
            }, 0);
            return classWriter.toByteArray();
        }
        if (className.equals("net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl")
                || className.equals("net.minecraft.client.multiplayer.ClientPacketListener")) {
            ClassReader classReader = new ClassReader(bytes);
            ClassWriter classWriter = new ClassWriter(0);
            classReader.accept(new ClassVisitor(ASM_BUILD, classWriter) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    if (name.equals("handleLogin")) {
                        return new MethodVisitor(ASM_BUILD, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                            @Override
                            public void visitInsn(int opcode) {
                                if (opcode == RETURN) {
                                    super.visitVarInsn(ALOAD, 0);
                                    super.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/ClientHooks", "joinHook", "(Lnet/minecraft/client/multiplayer/ClientPacketListener;)V", false);
                                }
                                super.visitInsn(opcode);
                            }
                        };
                    } else if (name.equals("onDisconnect")) {
                        return new MethodVisitor(ASM_BUILD, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                            @Override
                            public void visitCode() {
                                super.visitCode();
                                super.visitVarInsn(ALOAD, 0);
                                super.visitMethodInsn(INVOKESTATIC, "net/puzzle_mod_loader/core/ClientHooks", "onDisconnect", "(Lnet/minecraft/network/chat/Component;)V", false);
                            }
                        };
                    } else {
                        return super.visitMethod(access, name, descriptor, signature, exceptions);
                    }
                }
            }, 0);
            return classWriter.toByteArray();
        }
        return bytes;
    }
}
