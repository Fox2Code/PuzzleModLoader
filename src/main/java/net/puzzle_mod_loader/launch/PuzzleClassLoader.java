package net.puzzle_mod_loader.launch;

import net.puzzle_mod_loader.launch.event.SubscribeEvent;
import net.puzzle_mod_loader.launch.rebuild.ClassDataProvider;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.LinkedList;

public final class PuzzleClassLoader extends URLClassLoader implements Opcodes {
    private final ClassLoader parent;
    private final LinkedList<String> exclusions;
    private final LinkedList<ClassTransformer> classTransformers;
    private final ClassDataProvider classDataProvider;

    PuzzleClassLoader(ClassLoader parent) throws ReflectiveOperationException {
        super(Java9Fix.getURLs(parent), null);
        this.parent = parent;
        this.exclusions = new LinkedList<>();
        this.classTransformers = new LinkedList<>();
        this.classDataProvider = new ClassDataProvider(this);
        this.addClassTransformers(Launch.compactTransformer);
        Java9Fix.fix(this);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith("net.puzzle_mod_loader.launch.")||
                name.startsWith("org.objectweb.asm.")) {
            return parent.loadClass(name);
        } else {
            try {
                return super.loadClass(name);
            } catch (ClassNotFoundException c) {
                if (name.startsWith("net.minecraft.") ||
                        name.startsWith("net.puzzle_mod_loader.")) {
                    throw c;
                }
                try {
                    return parent.loadClass(name);
                } catch (ClassNotFoundException ce) {
                    throw c;
                }
            }
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clas = this.findLoadedClass(name);
        if (clas!=null)
            return clas;
        byte[] bytes = null;
        try {
            final String packageName = name.lastIndexOf('.') == -1 ? "" : name.substring(0, name.lastIndexOf('.'));
            if (getPackage(packageName) == null) {
                definePackage(packageName, null, null, null, null, null, null, null);
            }
            if (name.startsWith("#")) {
                return this.createFactoryClass(name);
            }
            URL ressource = name.startsWith("net.puzzle_mod_loader.") ?
                    parent.getResource(name.replace('.', '/').concat(".class")) :
                    this.getResource(name.replace('.', '/').concat(".class"));
            if (ressource==null) throw new ClassNotFoundException(name);
            Launch.lastLoadedClass = System.currentTimeMillis();
            Launch.lastLoadedClassName = name;
            URLConnection urlConnection = ressource.openConnection();
            InputStream is = urlConnection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            bytes = buffer.toByteArray();
            String tmpName = name.replace('/','.');
            boolean exc = false;
            for (String excl:exclusions) {
                if (tmpName.startsWith(excl)) {
                    exc = true;
                    break;
                }
            }
            if (!exc) {
                for (ClassTransformer classTransformer : classTransformers) {
                    try {
                        bytes = classTransformer.transform(bytes, tmpName);
                    } catch (Throwable e) {
                        Files.write(new File(Launch.getHomeDir(), "transform_fail.class").toPath(), bytes);
                        throw new ClassTransformException("Can't transform "+name+" for "+classTransformer.getClass().getName(), e);
                    }
                }
                try {
                    ClassReader classReader = new ClassReader(bytes);
                    ClassWriter classWriter = classDataProvider.newClassWriter();
                    classReader.accept(classWriter, 0);
                    bytes = classWriter.toByteArray();
                } catch (Exception e) {
                    Files.write(new File(Launch.getHomeDir(), "compute_fail.class").toPath(), bytes);
                    throw new ClassTransformException("Can't compute frames for "+name, e);
                }
            } else {
                bytes = Launch.compactTransformer.transform(bytes, tmpName);
                ClassReader classReader = new ClassReader(bytes);
                ClassWriter classWriter = classDataProvider.newClassWriter();
                classReader.accept(classWriter, 0);
                bytes = classWriter.toByteArray();
            }
            URL url = null;
            if (urlConnection instanceof JarURLConnection) {
                url = ((JarURLConnection) urlConnection).getJarFileURL();
            }
            clas = defineClass(name,bytes,0,bytes.length,url == null ? null : new CodeSource(url,new CodeSigner[]{}));
            Launch.lastLoadedClass = System.currentTimeMillis();
            return clas;
        } catch (ClassFormatError ioe) {
            if (bytes != null) try {
                Files.write(new File(Launch.getHomeDir(), "load_fail.class").toPath(), bytes);
            } catch (IOException ignored) {}
            throw new ClassNotFoundException(name, ioe);
        } catch (ClassTransformException ioe) {
            ioe.printStackTrace();
            throw new ClassNotFoundException(name, ioe);
        } catch (Exception ioe) {
            throw new ClassNotFoundException(name, ioe);
        }
    }

    private Class<?> createFactoryClass(String name) throws ClassNotFoundException {
        ClassNode classNode;
        String asmName = name.replace('.', '/');
        String asmNameP = asmName.substring(1, asmName.length()-8);
        if (name.endsWith("#Handler")) {
            classNode = new ClassNode();
            classNode.visit(V1_8, ACC_PUBLIC|ACC_ABSTRACT|ACC_INTERFACE, name.replace('.', '/'), null, "java/lang/Object", new String[]{"net/puzzle_mod_loader/events/EventManager$Handler"});
            classNode.methods.add(new MethodNode(ACC_PUBLIC|ACC_ABSTRACT, "onEvent", "(L"+asmNameP+";)V", null, null));
            MethodNode methodNode = new MethodNode(ACC_PUBLIC|ACC_SYNTHETIC|ACC_BRIDGE, "onEvent", "(Lnet/puzzle_mod_loader/events/Event;)V", null, null);
            methodNode.instructions.add(new VarInsnNode(ALOAD, 0));
            methodNode.instructions.add(new VarInsnNode(ALOAD, 1));
            methodNode.instructions.add(new TypeInsnNode(CHECKCAST, asmNameP));
            methodNode.instructions.add(new MethodInsnNode(INVOKEINTERFACE, asmName, "onEvent", "(L"+asmNameP+";)V"));
            methodNode.instructions.add(new InsnNode(RETURN));
            classNode.methods.add(methodNode);
        } else if (name.endsWith("#Factory")) {
            Class<?> cl = Class.forName(asmNameP.replace('/', '.'));
            classNode = new ClassNode();
            classNode.visit(V1_8, ACC_PUBLIC, name.replace('.', '/'), null, "java/lang/Object", null);
            MethodNode methodNode = new MethodNode(ACC_PUBLIC|ACC_STATIC, "registerHandlers", "(L"+asmNameP+";)V", null, null);
            for (Method method:cl.getDeclaredMethods()) {
                if (Modifier.isAbstract(method.getModifiers()) || method.isSynthetic()) {
                    continue;
                }
                SubscribeEvent subscribeEvent =method.getAnnotation(SubscribeEvent.class);
                if (subscribeEvent == null) {
                    continue;
                }
                if (method.getParameterTypes().length != 1) {
                    continue;
                }
                Class<?> event = method.getParameterTypes()[0];
                String asmEvent = event.getName().replace('.','/');
                methodNode.instructions.add(new LdcInsnNode(Type.getType(event)));
                methodNode.instructions.add(new InsnNode(ICONST_0+subscribeEvent.priority().ordinal()));
                methodNode.instructions.add(new InsnNode(subscribeEvent.ignoreCanceled()?ICONST_1:ICONST_0));
                methodNode.instructions.add(new VarInsnNode(ALOAD, 0));
                methodNode.instructions.add(new InvokeDynamicInsnNode("onEvent", "(L"+asmNameP+";)L#"+asmEvent+"#Handler;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false),
                        Type.getType("(L"+asmEvent+";)V"), new Handle(Opcodes.H_INVOKEVIRTUAL, asmNameP, method.getName(), "(L"+asmEvent+";)V", false), Type.getType("(L"+asmEvent+";)V")));
                methodNode.instructions.add(new MethodInsnNode(INVOKESTATIC, "net/puzzle_mod_loader/events/EventManager", "registerHandler", "(Ljava/lang/Class;IZLnet/puzzle_mod_loader/events/EventManager$Handler;)V", false));
            }
            methodNode.instructions.add(new InsnNode(RETURN));
            classNode.methods.add(methodNode);
        } else {
            throw new ClassNotFoundException(name);
        }
        ClassWriter classWriter = classDataProvider.newClassWriter();
        classNode.accept(classWriter);
        byte[] bytes = classWriter.toByteArray();
        try {
            return defineClass(name,bytes,0,bytes.length);
        } catch (ClassFormatError e) {
            try {
                Files.write(new File(Launch.getHomeDir(), "gen_fail.class").toPath(), bytes);
            } catch (IOException ignored) {}
            throw e;
        }
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    public boolean isClassLoaded(String className) {
        return this.findLoadedClass(className) != null;
    }

    public void addClassTransformers(ClassTransformer classTransformer) {
        String pkg = classTransformer.getClass().getPackage().getName();
        if (!pkg.startsWith("net.puzzle_mod_loader.launch")&&!exclusions.contains(pkg)) {
            exclusions.add(pkg);
        }
        classTransformers.add(classTransformer);
    }

    public int getClassTransformersCount() {
        return classTransformers.size();
    }

    public void addTransformerExclusion(String exclusion) {
        exclusions.add(exclusion);
    }

    public ClassDataProvider getClassDataProvider() {
        return classDataProvider;
    }

    static {
        ClassLoader.registerAsParallelCapable();
    }

    private static class ClassTransformException extends Exception {
        public ClassTransformException(String message, Throwable e) {
            super(message, e);
        }
    }
}
