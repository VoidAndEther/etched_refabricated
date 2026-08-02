package gg.moonflower.etched.registry.poi;

import gg.moonflower.etched.Etched;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class EtchedPoiTypes {
    public static final PoiType BARD = register("bard", Blocks.NOTE_BLOCK);
    public static final ResourceKey<PoiType> BARD_KEY = registerKey("bard");

    public static PoiType register(String name, Block ...blocks) {
        return PointOfInterestHelper.register(Etched.id(name), 1, 1, blocks);
    }
    public static ResourceKey<PoiType> registerKey(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Etched.id(name));
    }
    public static void register() {
    }
}
