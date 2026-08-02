package gg.moonflower.etched.mixin.client.render;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import gg.moonflower.etched.registry.item.BoomboxItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends LivingEntity> {

    @Final
    @Shadow
    public ModelPart leftArm;

    @Final
    @Shadow
    public ModelPart rightArm;

    // TODO: fix arm swing when holding a boombox

    @Unique
    private static @Nullable HumanoidArm getPlayingArm(LivingEntity entity) {
        InteractionHand playingHand = BoomboxItem.getPlayingHand(entity);
        if (playingHand == null) {
            return null;
        }

        switch (entity.getMainArm()) {
            case LEFT -> {
                return playingHand == InteractionHand.MAIN_HAND ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
            }
            case RIGHT -> {
                return playingHand == InteractionHand.MAIN_HAND ? HumanoidArm.RIGHT : HumanoidArm.LEFT;
            }
        }
        return null;
    }


    @WrapMethod(method = "poseRightArm")
    public void poseRightArm(T livingEntity, Operation<Void> original) {
        if (getPlayingArm(livingEntity) == HumanoidArm.RIGHT) {
            this.rightArm.xRot = (float) Math.PI;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = -0.610865F;
        } else {
            original.call(livingEntity);
        }
    }

    @WrapMethod(method = "poseLeftArm")
    public void poseLeftArm(T livingEntity, Operation<Void> original) {
        if (getPlayingArm(livingEntity) == HumanoidArm.LEFT) {
            this.leftArm.xRot = (float) Math.PI;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.610865F;
        } else {
            original.call(livingEntity);
        }
    }

    @Inject(method = "setupAttackAnimation", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/geom/ModelPart;xRot:F", ordinal = 1, opcode = Opcodes.PUTFIELD), cancellable = true)
    public void setupAttackAnimation(T livingEntity, float f, CallbackInfo ci, @Local HumanoidArm arm) {
        if (getPlayingArm(livingEntity) == arm) {
            ci.cancel();
        }
    }
}
