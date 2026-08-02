package gg.moonflower.etched.registry.profession;

import com.google.common.collect.ImmutableSet;
import gg.moonflower.etched.Etched;
import gg.moonflower.etched.registry.block.EtchedBlocks;
import gg.moonflower.etched.registry.item.EtchedItems;
import gg.moonflower.etched.registry.poi.EtchedPoiTypes;
import gg.moonflower.etched.mixin.StructureTemplatePoolAccessor;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class EtchedVillagerProfessions {
    public static final VillagerProfession BARD = register("bard", EtchedPoiTypes.BARD_KEY, SoundEvents.VILLAGER_WORK_LIBRARIAN);
    @SuppressWarnings("SameParameterValue")
    private static VillagerProfession register(String name, ResourceKey<PoiType> type, SoundEvent sound) {
        Predicate<Holder<PoiType>> predicate = entry -> entry.is(type);
        return Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, Etched.id(name), new VillagerProfession(name, predicate, predicate, ImmutableSet.of(), ImmutableSet.of(), sound));
    }
    public static void register() {
        TradeOfferHelper.registerVillagerOffers(EtchedVillagerProfessions.BARD, 1, factories -> {
            Sell.Builder builder = Sell.builder(1, 8, 4, 20);
            factories.add(builder.apply(Items.MUSIC_DISC_13));
            factories.add(builder.apply(Items.MUSIC_DISC_11));
            factories.add(builder.apply(Items.MUSIC_DISC_CAT));
            factories.add(builder.apply(Items.MUSIC_DISC_OTHERSIDE));
            factories.add(new Sell(Items.NOTE_BLOCK, 2, 1, 16, 2));
            factories.add(new Buy(EtchedItems.MUSIC_LABEL, 2, 4, 16, 1));
        });
        TradeOfferHelper.registerVillagerOffers(EtchedVillagerProfessions.BARD, 2, factories -> {
            factories.add(new Buy(EtchedItems.BLANK_MUSIC_DISC, 2, 28, 12, 15));
            factories.add(new Buy(EtchedBlocks.ETCHING_TABLE, 1, 32, 8, 15));
        });
        TradeOfferHelper.registerVillagerOffers(EtchedVillagerProfessions.BARD, 3, factories -> {
            factories.add(new Buy(Blocks.CLAY, 1, 6, 16, 2));
            factories.add(new Buy(Blocks.HAY_BLOCK, 1, 12, 8, 2));
            factories.add(new Buy(Blocks.WHITE_WOOL, 1, 8, 32, 4));
            factories.add(new Buy(Blocks.BONE_BLOCK, 1, 24, 8, 4));
            factories.add(new Buy(Blocks.PACKED_ICE, 1, 36, 4, 8));
            factories.add(new Buy(Blocks.GOLD_BLOCK, 1, 48, 2, 10));
            factories.add(new Buy(Items.JUKEBOX, 1, 26, 4, 30));
        });
        TradeOfferHelper.registerVillagerOffers(EtchedVillagerProfessions.BARD, 4, factories -> {
            factories.add(new Buy(EtchedItems.JUKEBOX_MINECART, 1, 28, 4, 30));
            factories.add(new Buy(EtchedBlocks.ALBUM_JUKEBOX, 1, 30, 4, 30));
        });
        TradeOfferHelper.registerVillagerOffers(EtchedVillagerProfessions.BARD, 5, factories -> {
            factories.add(new Sell(Items.DIAMOND, 1, 8, 8, 40));
            factories.add(new Sell(Items.AMETHYST_SHARD, 8, 1, 10, 40));
            BuiltInRegistries.ITEM.getTag(ItemTags.CREEPER_DROP_MUSIC_DISCS).ifPresent(tag -> tag.stream().map(Holder::value).map(Sell.builder(1, 10, 4, 40)).forEach(factories::add));
        });
        ServerLifecycleEvents.SERVER_STARTING.register(EtchedVillagerProfessions::onServerStarting);
    }

    private record Sell(ItemLike item, int cost, int amount, int uses, int exp) implements VillagerTrades.ItemListing {
        @Override
        public @NotNull MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
            return new MerchantOffer(new ItemCost(item, cost), new ItemStack(Items.EMERALD, amount), uses, exp, 0.05F);
        }
        public static Builder builder(int cost, int amount, int uses, int exp) {
            return new Builder(cost, amount, uses, exp);
        }
        record Builder(int cost, int amount, int uses, int exp) implements Function<ItemLike, Sell> {
            @Override
            public Sell apply(ItemLike item) {
                return new Sell(item, cost, amount, uses, exp);
            }
        }
    }
    private record Buy(ItemLike item, int amount, int cost, int uses, int exp) implements VillagerTrades.ItemListing {
        @Override
        public @NotNull MerchantOffer getOffer(Entity entity, RandomSource randomSource) {
            return new MerchantOffer(new ItemCost(Items.EMERALD, cost), new ItemStack(item, amount), uses, exp, 0.05F);
        }
    }

    private static void onServerStarting(MinecraftServer minecraftServer) {
        RegistryAccess.Frozen access = minecraftServer.registryAccess();
        Optional<Registry<StructureTemplatePool>> templateRegistryOptional = access.registry(Registries.TEMPLATE_POOL);
        Optional<Registry<StructureProcessorList>> processorListRegistyOptional = access.registry(Registries.PROCESSOR_LIST);

        if (templateRegistryOptional.isEmpty() || processorListRegistyOptional.isEmpty()) {
            return;
        }

        Registry<StructureTemplatePool> pools = templateRegistryOptional.get();
        Registry<StructureProcessorList> lists = processorListRegistyOptional.get();
        createVillagePiece(pools, lists, "plains", "bard_house", 1, 2, ProcessorLists.MOSSIFY_10_PERCENT, ProcessorLists.ZOMBIE_PLAINS);
        createVillagePiece(pools, lists, "desert", "bard_house", 1, 2, ProcessorLists.ZOMBIE_DESERT);
        createVillagePiece(pools, lists, "savanna", "bard_house", 1, 4, ProcessorLists.ZOMBIE_SAVANNA);
        createVillagePiece(pools, lists, "snowy", "bard_house", 1, 4, ProcessorLists.ZOMBIE_SNOWY);
        createVillagePiece(pools, lists, "taiga", "bard_house", 1, 4, ProcessorLists.MOSSIFY_10_PERCENT, ProcessorLists.ZOMBIE_TAIGA);
    }
    @SuppressWarnings("SameParameterValue")
    private static void createVillagePiece(Registry<StructureTemplatePool> templatePools, Registry<StructureProcessorList> processorLists, String village, String name, int houseId, int weight, ResourceKey<StructureProcessorList> zombieProcessor) {
        createVillagePiece(templatePools, processorLists, village, name, houseId, weight, ResourceKey.create(Registries.PROCESSOR_LIST, ResourceLocation.withDefaultNamespace(name)), zombieProcessor);
    }
    private static void createVillagePiece(Registry<StructureTemplatePool> templatePools, Registry<StructureProcessorList> processorLists, String village, String name, int houseId, int weight, ResourceKey<StructureProcessorList> normalProcessor, ResourceKey<StructureProcessorList> zombieProcessor) {
        addToPool(templatePools.get(ResourceLocation.withDefaultNamespace("village/" + village + "/houses")), Etched.id("village/" + village + "/houses/" + village + "_" + name + "_" + houseId), processorLists.getHolder(normalProcessor).orElse(null), weight);
        addToPool(templatePools.get(ResourceLocation.withDefaultNamespace("village/" + village + "/zombie/houses")), Etched.id("village/" + village + "/houses/" + village + "_" + name + "_" + houseId), processorLists.getHolder(zombieProcessor).orElse(null), weight);
    }
    private static void addToPool(@Nullable StructureTemplatePool pool, ResourceLocation pieceId, @Nullable Holder<StructureProcessorList> processorList, int weight) {
        if (pool == null || processorList == null) {
            return;
        }

        StructurePoolElement piece = StructurePoolElement.legacy(pieceId.toString(), processorList).apply(StructureTemplatePool.Projection.RIGID);
        List<StructurePoolElement> templates = ((StructureTemplatePoolAccessor) pool).getTemplates();
        if (templates == null) {
            return;
        }

        for (int i = 0; i < weight; i++) {
            templates.add(piece);
        }
    }
}
