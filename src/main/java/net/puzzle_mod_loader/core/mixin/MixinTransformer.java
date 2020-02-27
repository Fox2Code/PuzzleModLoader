package net.puzzle_mod_loader.core.mixin;

import net.puzzle_mod_loader.core.ModLoader;
import net.puzzle_mod_loader.launch.ClassTransformer;
import net.puzzle_mod_loader.launch.Java9Fix;
import net.puzzle_mod_loader.launch.Launch;
import net.puzzle_mod_loader.utils.ReflectedClass;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.transformer.Config;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;

public class MixinTransformer implements ClassTransformer {
    private static final boolean ABUSE_LOGGER = false; // If you need to breaks Mixins
    private static final boolean FORCE_DOT = true; // Fix dot for Mixins

    static MixinTransformer INSTANCE;

    static {
        try {
            Launch.getClassLoader().addClassTransformers(INSTANCE = new MixinTransformer());
        } catch (ReflectiveOperationException e) {
            throw new Error("Init failed!", e);
        }
    }

    public static void init0() {}

    IMixinTransformer mixinTransformer;
    ReflectedClass configs;

    private MixinTransformer() throws ReflectiveOperationException {
        MixinBootstrap.init();
        MixinEnvironment.getCurrentEnvironment().setOption(MixinEnvironment.Option.DISABLE_REFMAP, true);
        MixinEnvironment.getCurrentEnvironment().setOption(MixinEnvironment.Option.IGNORE_REQUIRED, true);
        MixinEnvironment.getCurrentEnvironment().setOption(MixinEnvironment.Option.DEBUG_INJECTORS, true);
        if (isDev()) {
            MixinEnvironment.getCurrentEnvironment().setOption(MixinEnvironment.Option.DEBUG_VERBOSE, true);
        }
        MixinEnvironment.getCurrentEnvironment().setSide(Launch.isClient() ? MixinEnvironment.Side.CLIENT : MixinEnvironment.Side.SERVER);
        MixinBootstrap.getPlatform().inject();
        mixinTransformer = (IMixinTransformer) MixinEnvironment.getCurrentEnvironment().getActiveTransformer();
        if (mixinTransformer == null) {
            Constructor<? extends IMixinTransformer> mixinTransformerConstructor =
                Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer")
                        .asSubclass(IMixinTransformer.class).getConstructor();
            Java9Fix.setAccessible(mixinTransformerConstructor);
            mixinTransformer = mixinTransformerConstructor.newInstance();
        }
        try {
            configs = ReflectedClass.of(this.mixinTransformer)
                    .get("processor").get("configs");
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
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

        if (ABUSE_LOGGER) {
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
        }

        return mixinTransformer.transformClassBytes(className, className, bytes);
    }

    public static void forceReload() {
        try {
            boolean pass = false;
            do {
                Config[] configs = Mixins.getConfigs().toArray(new Config[0]);
                Mixins.getConfigs().clear();
                for (Config config:configs) {
                    ReflectedClass configHandle = ReflectedClass.of(config).run("get");
                    try {
                        if (FORCE_DOT) {
                            String mixinPackage = configHandle.get("mixinPackage").asString();
                            if (!mixinPackage.endsWith(".")) {
                                configHandle.set("mixinPackage", mixinPackage + ".");
                            }
                        }
                        configHandle.set0("env", MixinEnvironment.getCurrentEnvironment());
                        configHandle.run0("onSelect");
                        configHandle.run0("prepare");
                        INSTANCE.configs.run0("add", configHandle.getHandle());
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            } while (!pass && !Mixins.getConfigs().isEmpty() && (pass = true));
            if (!Mixins.getConfigs().isEmpty()) {
                ModLoader.LOGGER.warn("Mixin configs still exists after 2 pass: "+Mixins.getConfigs().toString());
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
