package gg.moonflower.etched.datagen;

import gg.moonflower.etched.registry.block.EtchedBlocks;
import gg.moonflower.etched.util.EtchedBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class EtchedBlockTagsProvider extends FabricTagProvider.BlockTagProvider {
    public EtchedBlockTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(EtchedBlockTags.RECORD_PLAYERS)
                .add(EtchedBlocks.ALBUM_JUKEBOX, EtchedBlocks.RADIO, Blocks.JUKEBOX);
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
                .add(EtchedBlocks.ETCHING_TABLE, EtchedBlocks.ALBUM_JUKEBOX, EtchedBlocks.RADIO);
        getOrCreateTagBuilder(ConventionalBlockTags.VILLAGER_JOB_SITES)
                .add(Blocks.NOTE_BLOCK);
    }
}
