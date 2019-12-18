package net.puzzle_mod_loader.core.mixin;

import net.puzzle_mod_loader.launch.ClassTransformer;
import net.puzzle_mod_loader.launch.Java9Fix;
import net.puzzle_mod_loader.launch.Launch;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;

import java.lang.reflect.Constructor;

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

    private MixinTransformer() throws ReflectiveOperationException {
        MixinBootstrap.init();
        MixinEnvironment.init(MixinEnvironment.Phase.DEFAULT);
        MixinEnvironment.getCurrentEnvironment().setOption(MixinEnvironment.Option.DISABLE_REFMAP, true);
        MixinEnvironment.getCurrentEnvironment().setOption(MixinEnvironment.Option.IGNORE_REQUIRED, true);
        MixinEnvironment.getCurrentEnvironment().setOption(MixinEnvironment.Option.DEBUG_INJECTORS, true);
        MixinEnvironment.getCurrentEnvironment().setSide(Launch.isClient() ? MixinEnvironment.Side.CLIENT : MixinEnvironment.Side.SERVER);
        Constructor<? extends IMixinTransformer> mixinTransformerConstructor =
                Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer")
                        .asSubclass(IMixinTransformer.class).getConstructor();
        Java9Fix.setAccessible(mixinTransformerConstructor);
        mixinTransformer = mixinTransformerConstructor.newInstance();
    }

    @Override
    public byte[] transform(byte[] bytes, String className) {
        return mixinTransformer.transformClassBytes(className, className, bytes);
    }
}
