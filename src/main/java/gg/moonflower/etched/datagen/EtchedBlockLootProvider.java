package gg.moonflower.etched.datagen;

import gg.moonflower.etched.registry.block.RadioBlock;
import gg.moonflower.etched.registry.block.EtchedBlocks;
import gg.moonflower.etched.registry.item.EtchedItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.concurrent.CompletableFuture;

public class EtchedBlockLootProvider extends FabricBlockLootTableProvider {


    public EtchedBlockLootProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        dropSelf(EtchedBlocks.ALBUM_JUKEBOX);
        dropSelf(EtchedBlocks.ETCHING_TABLE);
        add(EtchedBlocks.RADIO, block -> LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(
                                        this.applyExplosionCondition(
                                                block,
                                                AlternativesEntry.alternatives(
                                                        LootItem.lootTableItem(EtchedItems.PORTAL_RADIO)
                                                                .when(
                                                                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                                                .setProperties(
                                                                                        StatePropertiesPredicate.Builder.properties()
                                                                                                .hasProperty(RadioBlock.PORTAL, true)
                                                                                )
                                                                ),
                                                        LootItem.lootTableItem(EtchedBlocks.RADIO)
                                                )

                                        )
                                )
                ));
    }
}
