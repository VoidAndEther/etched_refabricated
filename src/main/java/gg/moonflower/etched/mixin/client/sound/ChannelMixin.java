package gg.moonflower.etched.mixin.client.sound;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.audio.Channel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Channel.class)
public class ChannelMixin {

    @Unique
    private final AtomicBoolean loaded = new AtomicBoolean(false);
    @Unique
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    @WrapMethod(method = "stop")
    public void stop(Operation<Void> original) {
        if (!this.loaded.get()) {
            this.stopped.set(true);
        }
        original.call();
    }

    @WrapMethod(method = "play")
    public void play(Operation<Void> original) {
        if (this.stopped.get()) {
            this.stopped.set(false);
        } else {
            original.call();
        }
    }

    @WrapMethod(method = "stopped")
    public boolean stopped(Operation<Boolean> original) {
        return original.call() || this.stopped.get();
    }

    @Inject(method = {"attachStaticBuffer", "attachBufferStream"}, at = @At("HEAD"))
    public void attachStaticBuffer(CallbackInfo ci) {
        this.loaded.set(true);
    }
}
