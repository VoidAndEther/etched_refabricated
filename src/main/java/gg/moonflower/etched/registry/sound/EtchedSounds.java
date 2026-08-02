package gg.moonflower.etched.registry.sound;

import gg.moonflower.etched.Etched;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;


public class EtchedSounds {
    public static final SoundEvent UI_ETCHER_TAKE_RESULT = register("ui.etching_table.take_result");

    @SuppressWarnings("SameParameterValue")
    public static SoundEvent register(String name) {
        ResourceLocation id = Etched.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, Etched.id(name), SoundEvent.createVariableRangeEvent(id));
    }
    public static void register() {
    }
}
