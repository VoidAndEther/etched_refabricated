package gg.moonflower.etched.registry.block.entity;

import gg.moonflower.etched.Etched;
import gg.moonflower.etched.registry.block.EtchedBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class EtchedBlockEntities {
    public static final BlockEntityType<AlbumJukeboxBlockEntity> ALBUM_JUKEBOX = register("album_jukebox", AlbumJukeboxBlockEntity::new, EtchedBlocks.ALBUM_JUKEBOX);
    public static final BlockEntityType<RadioBlockEntity> RADIO = register("radio", RadioBlockEntity::new, EtchedBlocks.RADIO);

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Block... blocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Etched.id(name), BlockEntityType.Builder.of(factory, blocks).build());
    }
    public static void register() {
    }
}
