package gg.moonflower.etched.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gg.moonflower.etched.registry.component.AlbumCoverComponent;
import gg.moonflower.etched.registry.component.EtchedComponents;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GrindstoneMenu.class)
public class GrindstoneMenuMixin {
    @WrapOperation(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/GrindstoneMenu;computeResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack createResult(GrindstoneMenu instance, ItemStack top, ItemStack bottom, Operation<ItemStack> original){
        if (top.isEmpty() ^ bottom.isEmpty()) {
            ItemStack stack = top.isEmpty() ? bottom : top;
            AlbumCoverComponent albumCover = stack.get(EtchedComponents.ALBUM_COVER);
            if (albumCover != null && !albumCover.getCoverStack().isEmpty()) {
                ItemStack result = stack.copyWithCount(1);
                result.set(EtchedComponents.ALBUM_COVER, albumCover.toBuilder().setCoverStack(ItemStack.EMPTY).build());
                return result;
            }
        }
        return original.call(instance, top, bottom);
    }
}
