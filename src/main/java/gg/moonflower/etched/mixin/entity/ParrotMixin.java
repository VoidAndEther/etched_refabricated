package gg.moonflower.etched.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gg.moonflower.etched.api.sound.SoundTracker;
import gg.moonflower.etched.registry.item.BoomboxItem;
import gg.moonflower.etched.util.EtchedBlockTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Parrot.class)
public abstract class ParrotMixin extends Entity {

    @Shadow
    private BlockPos jukebox;
    @Shadow
    private boolean partyParrot;

    @Unique
    private BlockPos etched$musicPos;
    @Unique
    private boolean etched$dancing;

    public ParrotMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @WrapMethod(method = "aiStep")
    public void capture(Operation<Void> original) {
        this.etched$musicPos = this.jukebox;
        this.etched$dancing = this.partyParrot;
        original.call();
    }

    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/ShoulderRidingEntity;aiStep()V"))
    public void addAudioProviders(Parrot instance, Operation<Void> original) {
        if (this.etched$musicPos == null || this.distanceToSqr(this.etched$musicPos.getCenter()) > 12 || !this.level().getBlockState(this.etched$musicPos).is(EtchedBlockTags.RECORD_PLAYERS)) {
            this.partyParrot = false;
            this.jukebox = null;
        } else {
            this.partyParrot = this.etched$dancing;
            this.jukebox = this.etched$musicPos;
        }

        if (this.level().isClientSide()) {
            List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().inflate(3.45), entity -> {
                if (!entity.isAlive() || entity.isSpectator()) {
                    return false;
                }
                if (entity == Minecraft.getInstance().player && BoomboxItem.getPlayingHand((LivingEntity) entity) == null) {
                    return false;
                }

                return SoundTracker.getEntitySound(entity.getId()) != null;
            });

            if (!entities.isEmpty()) {
                this.partyParrot = true;
            }
        }
        original.call(instance);
    }
}
