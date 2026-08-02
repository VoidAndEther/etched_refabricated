package gg.moonflower.etched.mixin.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NativeImage.class)
public interface NativeImageAccessor {

    @Invoker("<init>")
    static NativeImage construct(NativeImage.Format format, int i, int j, boolean bl, long l) {
        throw new AssertionError();
    }
}
