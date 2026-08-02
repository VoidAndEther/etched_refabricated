package gg.moonflower.etched.mixin.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.api.sound.StopListeningSound;
import gg.moonflower.etched.client.GuiHook;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow
    private ClientLevel level;

    @Shadow
    protected abstract void notifyNearbyEntities(Level level, BlockPos blockPos, boolean bl);

    @WrapOperation(method = "playJukeboxSong", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;setNowPlaying(Lnet/minecraft/network/chat/Component;)V"))
    private void nowPlaying(Gui instance, Component component, Operation<Void> original, @Local(argsOnly = true) BlockPos pos) {
        if (!this.level.getBlockState(pos.above()).isAir() || !PlayableRecord.canShowMessage(Vec3.atCenterOf(pos))) {
            GuiHook.hidePlayingText = true;
        }
        original.call(instance, component);
        GuiHook.hidePlayingText = false;
    }

    @ModifyVariable(method = "playJukeboxSong", at = @At(value = "STORE", target = "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;forJukeboxSong(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"))
    public SoundInstance modifySoundInstance(SoundInstance sound, @Local(argsOnly = true) BlockPos pos) {
        return StopListeningSound.create(sound, () -> this.notifyNearbyEntities(this.level, pos, false));
    }
}
