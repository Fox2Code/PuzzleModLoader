package net.puzzle_mod_loader.core.mixin;

import net.puzzle_mod_loader.core.ModLoader;
import net.puzzle_mod_loader.launch.ClassTransformer;
import net.puzzle_mod_loader.launch.Java9Fix;
import net.puzzle_mod_loader.launch.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.service.ISyntheticClassInfo;
import org.spongepowered.asm.service.ISyntheticClassRegistry;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Files;


public class MixinTransformer implements ClassTransformer {
    static MixinTransformer mixinTransformerINSTANCE;

    static {
        try {
            Launch.getClassLoader().addClassTransformers(mixinTransformerINSTANCE = new MixinTransformer());
        } catch (ReflectiveOperationException e) {
            throw new Error("Init failed!", e);
        }
    }

    public static void init0() {}

    IMixinTransformer mixinTransformer;
    ISyntheticClassRegistry registry;

    private MixinTransformer() throws ReflectiveOperationException {
        MixinBootstrap.init();
        MixinEnvironment.getCurrentEnvironment().setOption(MixinEnvironment.Option.DISABLE_REFMAP, true);
        //MixinEnvironment.getCurrentEnvironment().setOption(MixinEnvironment.Option.IGNORE_REQUIRED, true);
        MixinEnvironment.getCurrentEnvironment().setOption(MixinEnvironment.Option.DEBUG_INJECTORS, true);
        if (isDev()) {
            MixinEnvironment.getCurrentEnvironment().setOption(MixinEnvironment.Option.DEBUG_ALL, true);
        }
        MixinEnvironment.getCurrentEnvironment().setSide(Launch.isClient() ? MixinEnvironment.Side.CLIENT : MixinEnvironment.Side.SERVER);
        MixinBootstrap.getPlatform().inject();
        MixinEnvironment.init(MixinEnvironment.Phase.DEFAULT);
        mixinTransformer = (IMixinTransformer) MixinEnvironment.getCurrentEnvironment().getActiveTransformer();
        if (mixinTransformer == null) {
            Constructor<? extends IMixinTransformer> mixinTransformerConstructor =
                Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer")
                        .asSubclass(IMixinTransformer.class).getConstructor();
            Java9Fix.setAccessible(mixinTransformerConstructor);
            mixinTransformer = mixinTransformerConstructor.newInstance();
        }
    }

    private static boolean isDev() {
        try {
            return new File(ModLoader.class
                    .getProtectionDomain().getCodeSource().getLocation().getFile()).isDirectory();
        } catch (NullPointerException npe) {
            return true;
        }
    }

    @Override
    public byte[] transform(byte[] bytes, String className) {
        if (className.startsWith("org.apache.logging.log4j.") || className.startsWith("net.puzzle_mod_loader.event.") ||
                className.startsWith("it.unimi.dsi.fastutil.")) {
            return bytes;
        }

        ModLoader.LOGGER.info(className);

        if (className.equals("net.puzzle_mod_loader.tests.MixinTest")) {

            ModLoader.LOGGER.info("V2");

            byte[] bytesMod = mixinTransformer.transformClassBytes(className, className, bytes);

            if (bytes == bytesMod) {
                throw new Error("Test Not Patched!");
            }

            try {
                Files.write(new File("./test_in.class").toPath(), bytes);
                Files.write(new File("./test_out.class").toPath(), bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bytesMod;
        }

        return mixinTransformer.transformClassBytes(className, className, bytes);
    }
}
