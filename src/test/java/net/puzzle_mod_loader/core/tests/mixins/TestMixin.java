package net.puzzle_mod_loader.core.tests.mixins;

import net.puzzle_mod_loader.tests.MixinTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@SuppressWarnings("OverwriteAuthorRequired")
@Mixin(MixinTest.class)
public class TestMixin {
    @Overwrite
    public static void test() throws Throwable {
        //No op
    }
}
