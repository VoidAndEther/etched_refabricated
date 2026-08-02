package gg.moonflower.etched.datagen;

import gg.moonflower.etched.registry.entity.EtchedEntities;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.minecraft.core.HolderLookup;


import java.util.concurrent.CompletableFuture;

public class EtchedEntityTypeTagsProvider extends FabricTagProvider.EntityTypeTagProvider {
    public EtchedEntityTypeTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(ConventionalEntityTypeTags.MINECARTS).add(EtchedEntities.JUKEBOX_MINECART);
    }
}
