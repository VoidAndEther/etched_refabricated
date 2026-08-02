package gg.moonflower.etched.util;

import gg.moonflower.etched.Etched;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class EtchedBlockTags {
    public static final TagKey<Block> RECORD_PLAYERS = register("record_players");
    @SuppressWarnings("SameParameterValue")
    private static TagKey<Block> register(String name) {
        return TagKey.create(Registries.BLOCK, Etched.id(name));
    }
}
