package gg.moonflower.etched.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gg.moonflower.etched.registry.item.BoomboxItem;
import gg.moonflower.etched.registry.component.EtchedComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public <T extends Entity> ItemEntityMixin(EntityType<T> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getItem();

    @WrapMethod(method = "tick")
    private void tick(Operation<Void> original) {
        ItemStack stack = this.getItem();
        if (this.level().isClientSide()) {
            BoomboxItem.updatePlaying(this.getId(), stack.has(EtchedComponents.PAUSED) ? ItemStack.EMPTY : BoomboxItem.getRecord(stack));
        }
        original.call();
    }
}
