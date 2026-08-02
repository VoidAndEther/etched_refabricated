package gg.moonflower.etched.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gg.moonflower.etched.registry.component.AlbumCoverComponent;
import gg.moonflower.etched.registry.component.EtchedComponents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = {"net.minecraft.world.inventory.GrindstoneMenu$3", "net.minecraft.world.inventory.GrindstoneMenu$4"})
public class GrindstoneSlotMixin {
    @WrapMethod(method = "mayPlace")
    private boolean mayPlace(ItemStack stack, Operation<Boolean> original) {
        AlbumCoverComponent albumCover = stack.get(EtchedComponents.ALBUM_COVER);
        return original.call(stack) && albumCover != null && albumCover.getCoverStack() != null;
    }
}