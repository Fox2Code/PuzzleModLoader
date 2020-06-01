package net.puzzle_mod_loader.launch.transformers;

import net.puzzle_mod_loader.launch.ClassTransformer;
import net.puzzle_mod_loader.launch.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class CompactTransformer implements ClassTransformer {
    private static final boolean CLIENT = Launch.isClient();
    private static final int MASK = ~(ACC_PROTECTED|ACC_PRIVATE|ACC_FINAL);
    private final ConcurrentHashMap<String, String> keys = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> remap = new ConcurrentHashMap<>();

    @Override
    public byte[] transform(byte[] bytes, String className) {
        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.SKIP_FRAMES);
        this.patchClassNode(classNode);
        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    public void patchClassNode(ClassNode classNode) {
        if (classNode.invisibleAnnotations != null) {
            for (AnnotationNode annotationNode : classNode.invisibleAnnotations) {
                if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/FallbackSuperclass;")) {
                    if (!Launch.hasClass(classNode.superName)) {
                        classNode.superName = ASMUtil.getTypeVal(annotationNode).getDescriptor();
                    }
                }
                if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/OptionalInterfaces;")) {
                    if (classNode.interfaces != null) {
                        classNode.interfaces.removeIf(s -> !Launch.hasClass(s));
                    }
                }
                if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/Implement;")) {
                    String i = ASMUtil.getStrVal(annotationNode);
                    if (Launch.hasClass(i)) {
                        if (classNode.interfaces == null) {
                            classNode.interfaces = new ArrayList<>();
                        }
                        classNode.interfaces.add(ASMUtil.getStrVal(annotationNode));
                    }
                }
                if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/ClientOnly;")) {
                    if (!CLIENT) {
                        classNode.innerClasses.clear();
                        classNode.methods.clear();
                        classNode.fields.clear();
                        classNode.interfaces = null;
                        classNode.superName = "java/lang/Object";
                    }
                }
                if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/PreLoad;")) {
                    String clName = ASMUtil.getTypeVal(annotationNode).getClassName();
                    if (Launch.hasClass(clName)) try {
                        Class.forName(clName, false, Launch.getClassLoader());
                    } catch (Throwable ignored) {}
                }
            }
            classNode.invisibleAnnotations.removeIf(a -> a.desc.startsWith("Lnet/puzzle_mod_loader/compact/"));
        }
        boolean mod = classNode.superName.equals("net/puzzle_mod_loader/core/Mod") && !CLIENT;
        if (!CLIENT && !mod) {
            if (classNode.interfaces != null && (classNode.interfaces.contains("net/puzzle_mod_loader/client/RendererManager$EntityRendererProvider"))) {
                classNode.interfaces.clear();
                classNode.fields.clear();
                classNode.methods.clear();
            }
        }
        boolean isMC = classNode.name.startsWith("net/minecraft/");
        boolean isInterface = (classNode.access&ACC_INTERFACE) != 0;
        boolean isEnum = (classNode.access&ACC_ENUM) != 0;
        if (isMC || isInterface) {
            classNode.access = ((classNode.access&MASK)|ACC_PUBLIC);
        }
        if (classNode.methods != null) {
            Iterator<MethodNode> methodIterator = classNode.methods.iterator();
            methods:
            while (methodIterator.hasNext()) {
                MethodNode methodNode = methodIterator.next();
                if (methodNode.localVariables != null) {
                    methodNode.localVariables.clear();
                }
                if (mod && (methodNode.name.equals("onClientInit") || methodNode.name.equals("onClientPostInit") ||
                        methodNode.desc.equals("(Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;Lnet/minecraft/world/entity/EntityType;)Lnet/minecraft/client/renderer/entity/EntityRenderer;"))) {
                    methodIterator.remove();
                    continue;
                }
                if (methodNode.invisibleAnnotations != null) {
                    for (AnnotationNode annotationNode : methodNode.invisibleAnnotations) {
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/Require;")) {
                            if (!Launch.hasClass(ASMUtil.getTypeVal(annotationNode).getDescriptor())) {
                                methodIterator.remove();
                                continue methods;
                            }
                        }
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/RemoveIf;")) {
                            if (Launch.hasClass(ASMUtil.getTypeVal(annotationNode).getDescriptor())) {
                                methodIterator.remove();
                                continue methods;
                            }
                        }
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/ClientOnly;")) {
                            if (!Launch.isClient()) {
                                methodIterator.remove();
                                continue methods;
                            }
                        }
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/BukkitOnly;")) {
                            if (!Launch.isBukkit()) {
                                methodIterator.remove();
                                continue methods;
                            }
                        }
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/Super;")) {
                            methodIterator.remove();
                            continue methods;
                        }
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/Constructor;")) {
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
                                desc = ASMUtil.getStrVal(annotationNode);
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
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/Rename;")) {
                            methodNode.name = ASMUtil.getStrVal(annotationNode);
                        }
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/Special;")) {
                            String str = ASMUtil.getStrVal(annotationNode);
                            if (!str.isEmpty()) {
                                for (String s:str.split("\\|")) {
                                    int i = s.indexOf("=");
                                    if (i == -1) {
                                        if (!this.keys.containsKey(str)) {
                                            methodIterator.remove();
                                            continue methods;
                                        }
                                    } else {
                                        if (!str.substring(i+1).equals(this.keys.get(str).substring(0, i))) {
                                            methodIterator.remove();
                                            continue methods;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    methodNode.invisibleAnnotations.removeIf(a -> a.desc.startsWith("Lnet/puzzle_mod_loader/compact/"));
                }
                if (isMC || methodNode.name.equals("<init>")) {
                    methodNode.access = ((methodNode.access&MASK)|ACC_PUBLIC);
                }
            }
        }
        if (classNode.fields != null) {
            Iterator<FieldNode> fieldsIterator = classNode.fields.iterator();
            fields:
            while (fieldsIterator.hasNext()) {
                FieldNode fieldNode = fieldsIterator.next();
                if (isEnum && (fieldNode.access & ACC_FINAL) != 0 && (fieldNode.name.equals("$VALUES") || fieldNode.name.equals("ENUM$VALUES"))) {
                    fieldNode.access &= ~ ACC_FINAL;
                }
                if (fieldNode.invisibleAnnotations != null) {
                    for (AnnotationNode annotationNode : fieldNode.invisibleAnnotations) {
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/Require;")) {
                            if (!Launch.hasClass(ASMUtil.getTypeVal(annotationNode).getDescriptor())) {
                                fieldsIterator.remove();
                                continue fields;
                            }
                        }
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/RemoveIf;")) {
                            if (Launch.hasClass(ASMUtil.getTypeVal(annotationNode).getDescriptor())) {
                                fieldsIterator.remove();
                                continue fields;
                            }
                        }
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/ClientOnly;")) {
                            if (!Launch.isClient()) {
                                fieldsIterator.remove();
                                continue fields;
                            }
                        }
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/BukkitOnly;")) {
                            if (!Launch.isBukkit()) {
                                fieldsIterator.remove();
                                continue fields;
                            }
                        }
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/Super;")) {
                            fieldsIterator.remove();
                            continue fields;
                        }
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/Rename;")) {
                            fieldNode.name = ASMUtil.getStrVal(annotationNode);
                        }
                        if (annotationNode.desc.equals("Lnet/puzzle_mod_loader/compact/Special;")) {
                            String str = ASMUtil.getStrVal(annotationNode);
                            if (!str.isEmpty()) {
                                for (String s:str.split("\\|")) {
                                    int i = s.indexOf("=");
                                    if (i == -1) {
                                        if (!this.keys.containsKey(str)) {
                                            fieldsIterator.remove();
                                            continue fields;
                                        }
                                    } else {
                                        if (!str.substring(i+1).equals(this.keys.get(str).substring(0, i))) {
                                            fieldsIterator.remove();
                                            continue fields;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    fieldNode.invisibleAnnotations.removeIf(a -> a.desc.startsWith("Lnet/puzzle_mod_loader/compact/"));
                }
                if (isMC) {
                    fieldNode.access = ((fieldNode.access&MASK)|ACC_PUBLIC|(isInterface?ACC_FINAL:0));
                }
            }
        }
    }

    public void putKey(String key,String value) {
        this.keys.put(key, value);
    }

    public void injectTypeFixRule(String line) {
        while (line.endsWith(" ")) line = line.substring(0, line.length()-1);
        while (line.startsWith(" ")) line = line.substring(1);
        int a = line.indexOf(' ');
        int b = line.lastIndexOf(' ');
        if (a == b) return; // Skip invalid lines
        String pre = line.substring(0, a);
        String post = line.substring(b+1);
        String acc = line.substring(a+1, b);
        String tmp;
        switch (acc) {
            default:
                return; // Skip invalid lines
            case "<":
                tmp = pre;
                pre = post;
                post = tmp;
            case ">":
                if (!Launch.hasClass(post) && Launch.hasClass(pre)) {
                    this.remap.put(pre.replace('.', '/'), post.replace('.', '/'));
                }
                return;
            case "|":
                boolean preE = Launch.hasClass(pre);
                boolean postE = Launch.hasClass(post);
                if (preE != postE) {
                    if (preE) {
                        this.remap.put(post.replace('.', '/'), pre.replace('.', '/'));
                    } else {
                        this.remap.put(pre.replace('.', '/'), post.replace('.', '/'));
                    }
                }
        }
    }
}
