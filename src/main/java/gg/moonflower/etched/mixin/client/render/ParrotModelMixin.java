package gg.moonflower.etched.mixin.client.render;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gg.moonflower.etched.EtchedClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ParrotModel.class)
public abstract class ParrotModelMixin {

    @Shadow
    @Final
    private ModelPart head;

    @Shadow
    @Final
    private ModelPart body;

    @Shadow
    @Final
    private ModelPart leftWing;

    @Shadow
    @Final
    private ModelPart rightWing;

    @Shadow
    @Final
    private ModelPart tail;

    @Shadow
    private static ParrotModel.State getState(Parrot parrot) {
        return null;
    }

    @WrapMethod(method = "setupAnim(Lnet/minecraft/world/entity/animal/Parrot;FFFFF)V")
    public void setupDanceAnimation(Parrot entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Operation<Void> original) {
        original.call(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (getState(entity) != ParrotModel.State.PARTY || !EtchedClientConfig.INSTANCE.smoothParrotAnimation()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        float partialTicks = minecraft.getTimer().getGameTimeDeltaPartialTick(minecraft.level == null || !minecraft.level.tickRateManager().isEntityFrozen(entity));
        float f = Mth.cos(entity.tickCount + partialTicks);
        float f1 = Mth.sin(entity.tickCount + partialTicks);
        this.head.x = f;
        this.head.y = 15.69F + f1;
        this.head.xRot = 0.0F;
        this.head.yRot = 0.0F;
        this.head.zRot = Mth.sin(entity.tickCount + partialTicks) * 0.4F;
        this.body.x = f;
        this.body.y = 16.5F + f1;
        this.leftWing.zRot = -0.0873F - ageInTicks;
        this.leftWing.x = 1.5F + f;
        this.leftWing.y = 16.94F + f1;
        this.rightWing.zRot = 0.0873F + ageInTicks;
        this.rightWing.x = -1.5F + f;
        this.rightWing.y = 16.94F + f1;
        this.tail.x = f;
        this.tail.y = 21.07F + f1;
    }
}
