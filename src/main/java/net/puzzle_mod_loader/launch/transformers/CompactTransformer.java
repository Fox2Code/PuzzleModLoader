package net.puzzle_mod_loader.launch.transformers;

import net.puzzle_mod_loader.launch.ClassTransformer;
import net.puzzle_mod_loader.launch.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Iterator;

public class CompactTransformer implements ClassTransformer {
    private static final boolean CLIENT = Launch.isClient();

    @Override
    public byte[] transform(byte[] bytes, String className) {
        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);
        if ((classNode.access&ACC_INTERFACE) != 0) {
            classNode.access = (classNode.access&~(ACC_PRIVATE|ACC_PROTECTED))|ACC_PUBLIC;
        }
        if (classNode.invisibleTypeAnnotations != null) {
            for (TypeAnnotationNode annotationNode : classNode.invisibleTypeAnnotations) {
                if (annotationNode.desc.equals("net/puzzle_mod_loader/compact/FallbackSuperclass")) {
                    if (!Launch.hasClass(classNode.superName)) {
                        classNode.superName = ((Type) annotationNode.values.get(0)).getDescriptor();
                    }
                }
                if (annotationNode.desc.equals("net/puzzle_mod_loader/compact/OptionalInterfaces")) {
                    classNode.interfaces.removeIf(s -> !Launch.hasClass(s));
                }
                if (annotationNode.desc.equals("net/puzzle_mod_loader/compact/Implement")) {
                    if (classNode.interfaces == null) {
                        classNode.interfaces = new ArrayList<>();
                    }
                    classNode.interfaces.add(annotationNode.values.get(0).toString());
                }
            }
        }
        boolean mod = classNode.superName.equals("net/puzzle_mod_loader/core/Mod") && !CLIENT;
        if (!CLIENT && !mod) {
            if (classNode.interfaces != null && (classNode.interfaces.contains("net/puzzle_mod_loader/client/RendererManager$EntityRendererProvider"))) {
                classNode.interfaces.clear();
                classNode.fields.clear();
                classNode.methods.clear();
            }
        }
        if (classNode.methods != null) {
            Iterator<MethodNode> methodIterator = classNode.methods.iterator();
            methods:
            while (methodIterator.hasNext()) {
                MethodNode methodNode = methodIterator.next();
                if (mod && (methodNode.name.equals("onClientInit") || methodNode.name.equals("onClientPostInit") ||
                        methodNode.desc.equals("(Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;Lnet/minecraft/world/entity/EntityType;)Lnet/minecraft/client/renderer/entity/EntityRenderer;"))) {
                    methodIterator.remove();
                    continue;
                }
                if (methodNode.invisibleTypeAnnotations != null) {
                    for (TypeAnnotationNode annotationNode : methodNode.invisibleTypeAnnotations) {
                        if (annotationNode.desc.equals("net/puzzle_mod_loader/compact/Require")) {
                            if (!Launch.hasClass(((Type) annotationNode.values.get(0)).getDescriptor())) {
                                methodIterator.remove();
                                continue methods;
                            }
                        }
                        if (annotationNode.desc.equals("net/puzzle_mod_loader/compact/RemoveIf")) {
                            if (Launch.hasClass(((Type) annotationNode.values.get(0)).getDescriptor())) {
                                methodIterator.remove();
                                continue methods;
                            }
                        }
                        if (annotationNode.desc.equals("net/puzzle_mod_loader/compact/ClientOnly")) {
                            if (!Launch.isClient()) {
                                methodIterator.remove();
                                continue methods;
                            }
                        }
                        if (annotationNode.desc.equals("net/puzzle_mod_loader/compact/Constructor")) {
                            String desc;
                            String owner = methodNode.desc.substring(methodNode.desc.indexOf(')') + 1);
                            if (!owner.startsWith("L")) {
                                throw new Error("Return value is not a valid class");
                            } else {
                                owner = owner.substring(1, owner.length() - 1);
                            }
                            if (annotationNode.values.isEmpty()) {
                                desc = methodNode.desc;
                                desc = desc.substring(desc.indexOf(')')) + ")V";
                            } else {
                                desc = annotationNode.values.get(0).toString();
                                if (desc.isEmpty()) {
                                    desc = methodNode.desc;
                                    desc = desc.substring(desc.indexOf(')')) + ")V";
                                }
                            }
                            Type[] types = Type.getArgumentTypes(desc);
                            InsnList insnList = new InsnList();
                            int i = 0;
                            for (Type type : types) {
                                char c = type.getDescriptor().charAt(0);
                                if (c == 'L') {
                                    insnList.add(new VarInsnNode(ALOAD, i));
                                } else {
                                    insnList.add(new VarInsnNode(ILOAD, i));
                                }
                                i++;
                            }
                            insnList.add(new TypeInsnNode(NEW, owner));
                            insnList.add(new InsnNode(DUP));
                            insnList.add(new MethodInsnNode(INVOKESPECIAL, owner, "<init>", desc, false));
                            insnList.add(new InsnNode(ARETURN));
                            methodNode.instructions = insnList;
                        }
                    }
                }
                if (methodNode.name.equals("<init>")) {
                    methodNode.access = (methodNode.access&~(ACC_PRIVATE|ACC_PROTECTED))|ACC_PUBLIC;
                }
            }
        }
        if (classNode.fields != null) {
            Iterator<FieldNode> fieldsIterator = classNode.fields.iterator();
            fields:
            while (fieldsIterator.hasNext()) {
                FieldNode fieldNode = fieldsIterator.next();
                if (fieldNode.invisibleTypeAnnotations != null) {
                    for (TypeAnnotationNode annotationNode : fieldNode.invisibleTypeAnnotations) {
                        if (annotationNode.desc.equals("net/puzzle_mod_loader/compact/Require")) {
                            if (!Launch.hasClass(((Type) annotationNode.values.get(0)).getDescriptor())) {
                                fieldsIterator.remove();
                                continue fields;
                            }
                        }
                        if (annotationNode.desc.equals("net/puzzle_mod_loader/compact/RemoveIf")) {
                            if (Launch.hasClass(((Type) annotationNode.values.get(0)).getDescriptor())) {
                                fieldsIterator.remove();
                                continue fields;
                            }
                        }
                        if (annotationNode.desc.equals("net/puzzle_mod_loader/compact/ClientOnly")) {
                            if (!Launch.isClient()) {
                                fieldsIterator.remove();
                                continue fields;
                            }
                        }
                    }
                }
            }
        }
        return bytes;
    }
}
