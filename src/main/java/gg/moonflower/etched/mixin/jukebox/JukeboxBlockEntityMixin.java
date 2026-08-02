package gg.moonflower.etched.mixin.jukebox;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.registry.network.play.ClientboundPlayBlockMusicPacket;
import gg.moonflower.etched.registry.component.EtchedComponents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin extends BlockEntity implements ContainerSingleItem {

    @Shadow
    private ItemStack item;

    @Shadow
    @Final
    private JukeboxSongPlayer jukeboxSongPlayer;

    @Shadow
    public abstract void onSongChanged();

    @Unique
    private boolean playing;

    public JukeboxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "setTheItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/JukeboxSongPlayer;stop(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    public void stop(ItemStack stack, CallbackInfo ci) {
        if (this.level instanceof ServerLevel serverLevel) {
            BlockPos pos = this.getBlockPos();
            if (PlayableRecord.isPlayableRecord(stack)) {
                ClientboundPlayBlockMusicPacket packet = new ClientboundPlayBlockMusicPacket(stack.copy(), pos);
                for (ServerPlayer player: PlayerLookup.around(serverLevel, pos.getCenter().add(0.5, 0.5, 0.5), 64.0)) {
                    ServerPlayNetworking.send(player, packet);
                }
                this.playing = true;
            } else if (this.playing) {
                this.playing = false;
                ((JukeboxSongPlayerAccessor) this.jukeboxSongPlayer).setTicksSinceSongStarted(0L);
                this.level.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, pos, GameEvent.Context.of(this.getBlockState()));
                this.level.levelEvent(1011, pos, 0);
                this.onSongChanged();
            }
        }
    }

    @WrapMethod(method = "getComparatorOutput")
    public int getComparatorOutput(Operation<Integer> original) {
        int result = original.call();
        return result == 0 && PlayableRecord.isPlayableRecord(this.item) ? 15 : result;
    }

    @WrapMethod(method = "canPlaceItem")
    public boolean canPlaceItem(int slot, ItemStack stack, Operation<Boolean> original) {
        return stack.has(EtchedComponents.DISC_APPEARANCE) && this.getItem(slot).isEmpty() || original.call(slot, stack);
    }
}
