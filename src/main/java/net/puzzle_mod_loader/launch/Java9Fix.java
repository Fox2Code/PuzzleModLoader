package net.puzzle_mod_loader.launch;

import com.fox2code.udk.build.Internal;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

@SuppressWarnings("JavaReflectionMemberAccess")
public class Java9Fix {
    private static final boolean java8 = System.getProperty("java.version").startsWith("1.");
    private static Object unsafe;
    private static Method getURLs;
    private static Method fieldOffset;
    private static Method fieldPutBool;
    private static Method fieldGetObject;
    private static Method getModule;
    private static long moduleDescOffset;
    private static long moduleDescOpenOffset;

    static {
        Class<?> URLClassPath;
        try {
            URLClassPath = Class.forName("sun.misc.URLClassPath");
        } catch (ClassNotFoundException e) {
            try {
                URLClassPath = Class.forName("jdk.internal.loader.URLClassPath");
            } catch (ClassNotFoundException ex) {
                throw new InternalError("Unable to find URLClassPath class", ex);
            }
        }
        Class<?> unsafeClass = null;
        try {
            unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = field.get(null);
            fieldOffset = unsafeClass.getDeclaredMethod("objectFieldOffset", Field.class);
            fieldPutBool = unsafeClass.getDeclaredMethod("putBoolean", Object.class, long.class, boolean.class);
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
        if (!java8) /* try */ {
            try {
                //Disable Java9+ Reflection Warnings
                Method putObjectVolatile = unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
                Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);
                Class<?> loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
                Field loggerField = loggerClass.getDeclaredField("logger");
                Long offset = (Long) staticFieldOffset.invoke(unsafe, loggerField);
                putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
            } catch (ReflectiveOperationException ignored) {
                System.out.println("[Java9Fix]: Unable to disable invalid access logging");
            }
            try {
                getModule = Class.class.getDeclaredMethod("getModule");
                Class<?> module = Class.forName("java.lang.Module");
                moduleDescOffset = (Long) fieldOffset.invoke(unsafe, module.getDeclaredField("descriptor"));
                Class<?> desc = Class.forName("java.lang.module.ModuleDescriptor");
                moduleDescOpenOffset = (Long) fieldOffset.invoke(unsafe, desc.getDeclaredField("open"));
                fieldGetObject = unsafeClass.getDeclaredMethod("getObject", Object.class, long.class);
                openModule(Class.class);
                openModule(URLClassPath);
            } catch (ReflectiveOperationException ignored) {
                System.out.println("[Java9Fix]: Unable to disable reflections checks");
            }
        } /* catch (ReflectiveOperationException e) {
            throw new InternalError("Couldn't init bypass!", e);
        } */
        try {
            getURLs = URLClassPath.getDeclaredMethod("getURLs");
            Java9Fix.setAccessible(getURLs);
        } catch (ReflectiveOperationException e) {
            throw new InternalError("Couldn't init bypass!", e);
        }
    }

    private static Field access;
    private static long accessOffset;

    public static void setAccessible(AccessibleObject field) throws ReflectiveOperationException {
        if (access == null) {
            access = AccessibleObject.class.getDeclaredField("override");
            accessOffset = (Long) fieldOffset.invoke(unsafe, access);
            fieldPutBool.invoke(unsafe, access, accessOffset, true);
        }
        fieldPutBool.invoke(unsafe, field, accessOffset, true);
    }

    public static void setBoolean(Object obj, Field field, boolean value) throws ReflectiveOperationException {
        fieldPutBool.invoke(unsafe, obj, (Long) fieldOffset.invoke(field, value), true);
    }

    public static void openModule(Class<?> cl) throws ReflectiveOperationException {
        if (!java8 && fieldGetObject != null) {
            Object tmp = getModule.invoke(cl);
            tmp = fieldGetObject.invoke(unsafe, tmp, moduleDescOffset);
            fieldPutBool.invoke(unsafe, tmp, moduleDescOpenOffset, true);
        }
    }

    public static URL[] getURLs(ClassLoader classLoader) throws ReflectiveOperationException {
        Class<?> ccl = classLoader.getClass();
        Field ucp = null;
        while (ucp == null && ccl != null) {
            try {
                ucp = ccl.getDeclaredField("ucp");
            } catch (ReflectiveOperationException e) {
                ccl = ccl.getSuperclass();
            }
        }
        if (ucp == null) {
            throw new NoSuchFieldException("Unable to find URLClassPath field in the current class loader");
        }
        Java9Fix.setAccessible(ucp);
        return  (URL[]) getURLs.invoke(ucp.get(classLoader));
    }

    public static boolean isJava8() {
        return java8;
    }

    public static boolean isJava9() {
        return !java8;
    }

    @Internal
    static void fix(ClassLoader classLoader) {
        try {
            Class<?> Reflection;
            try {
                Reflection = Class.forName("sun.reflect.Reflection");
            } catch (ClassNotFoundException e) {
                Reflection = Class.forName("jdk.internal.reflect.Reflection");
            }
            Method filter = Reflection.getDeclaredMethod("registerFieldsToFilter", Class.class, String[].class);
            filter.invoke(null, Java9Fix.class, new String[]{"java8"}); filter.invoke(null, classLoader.loadClass("net.puzzle_mod_loader.core.ModLoader"), new String[]{"cachedData", "mods"});filter.invoke(null, classLoader.loadClass("net.puzzle_mod_loader.core.Mod"), new String[]{"hash", "id", "version", "name", "file"}); filter.invoke(null, classLoader.loadClass("net.puzzle_mod_loader.core.ServerHelper"), new String[]{"onBlacklistedServer", "BLACKLIST"}); filter.invoke(null, classLoader.loadClass("net.puzzle_mod_loader.helper.ModInfo"), new String[]{"id", "display", "version", "hash", "signature", "flags"}); filter.invoke(null, classLoader.loadClass("net.puzzle_mod_loader.helper.ModList"), new String[]{"modInfos"});
        } catch (Throwable ignored) {}
    }
}
