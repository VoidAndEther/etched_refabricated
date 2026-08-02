package gg.moonflower.etched.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gg.moonflower.etched.client.GuiHook;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Gui.class, priority = 2000) // Make sure this is applied after other mixins
public class GuiMixin {
    @WrapMethod(method = "setNowPlaying")
    public void setNowPlaying(Component component, Operation<Void> original) {
        if (GuiHook.hidePlayingText) {
            return;
        }
        original.call(component);
    }
}
