package gg.moonflower.etched.datagen;

import gg.moonflower.etched.registry.item.EtchedItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;

import java.util.concurrent.CompletableFuture;

public class EtchedItemTagsProvider extends FabricTagProvider.ItemTagProvider {
    public EtchedItemTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(ItemTags.DYEABLE).add(EtchedItems.BLANK_MUSIC_DISC);
    }
}
