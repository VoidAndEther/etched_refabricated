package gg.moonflower.etched.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import gg.moonflower.etched.Etched;
import gg.moonflower.etched.registry.block.AlbumJukeboxBlock;
import gg.moonflower.etched.registry.block.EtchedBlocks;
import gg.moonflower.etched.registry.component.DiscAppearanceComponent;
import gg.moonflower.etched.registry.item.EtchedItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class EtchedModelProvider extends FabricModelProvider {


    public EtchedModelProvider(FabricDataOutput output) {
        super(output);
    }
    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        ResourceLocation empty = ModelTemplates.CUBE_ORIENTABLE.create(
                EtchedBlocks.ALBUM_JUKEBOX,
                new TextureMapping()
                    .put(TextureSlot.TOP, TextureMapping.getBlockTexture(EtchedBlocks.ALBUM_JUKEBOX, "_top"))
                    .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(EtchedBlocks.ALBUM_JUKEBOX, "_side"))
                    .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(EtchedBlocks.ALBUM_JUKEBOX, "_front")),
                blockModelGenerators.modelOutput
        );
        ResourceLocation full = ModelTemplates.CUBE_ORIENTABLE.create(
                ModelLocationUtils.getModelLocation(EtchedBlocks.ALBUM_JUKEBOX, "_full"),
                new TextureMapping()
                        .put(TextureSlot.TOP, TextureMapping.getBlockTexture(EtchedBlocks.ALBUM_JUKEBOX, "_top"))
                        .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(EtchedBlocks.ALBUM_JUKEBOX, "_side"))
                        .put(TextureSlot.FRONT, TextureMapping.getBlockTexture(EtchedBlocks.ALBUM_JUKEBOX, "_front_full")),
                blockModelGenerators.modelOutput
        );
        blockModelGenerators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(
                EtchedBlocks.ALBUM_JUKEBOX,
                Variant.variant().with(VariantProperties.UV_LOCK, true)
        ).with(BlockModelGenerators.createBooleanModelDispatch(AlbumJukeboxBlock.HAS_RECORD, full, empty))
         .with(BlockModelGenerators.createHorizontalFacingDispatch()));

        blockModelGenerators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(
                EtchedBlocks.ETCHING_TABLE,
                Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(EtchedBlocks.ETCHING_TABLE))
        ).with(BlockModelGenerators.createHorizontalFacingDispatch()));
    }
    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        ModelTemplates.TWO_LAYERED_ITEM.create(
                ModelLocationUtils.getModelLocation(EtchedItems.MUSIC_LABEL),
                new TextureMapping()
                        .put(TextureSlot.LAYER0, TextureMapping.getItemTexture(EtchedItems.MUSIC_LABEL, "_top"))
                        .put(TextureSlot.LAYER1, TextureMapping.getItemTexture(EtchedItems.MUSIC_LABEL, "_bottom")),
                generator.output
        );
        ModelBuilder disc = new ModelBuilder();
        generator.generateFlatItem(EtchedItems.BLANK_MUSIC_DISC, ModelTemplates.FLAT_ITEM);
        for (DiscAppearanceComponent.LabelPattern value : DiscAppearanceComponent.LabelPattern.values()) {
            Pair<ResourceLocation, ResourceLocation> textures = value.getTextures();
            ResourceLocation identifier = ModelLocationUtils.getModelLocation(EtchedItems.ETCHED_MUSIC_DISC, "_" + value.name().toLowerCase(Locale.ROOT));
            TextureMapping layer = TextureMapping.layer0(EtchedItems.ETCHED_MUSIC_DISC);
            UnaryOperator<String> slice = p -> p.substring(9, p.length() - 4);
            ResourceLocation pattern = value.isComplex() ?
                ModelTemplates.THREE_LAYERED_ITEM.create(
                        identifier,
                        layer.put(TextureSlot.LAYER1, textures.getFirst().withPath(slice))
                             .put(TextureSlot.LAYER2, textures.getSecond().withPath(slice)),
                        generator.output
                ) : ModelTemplates.TWO_LAYERED_ITEM.create(
                    identifier,
                    layer.put(TextureSlot.LAYER1, textures.getFirst().withPath(slice)),
                    generator.output
            );
            if (value.ordinal() == 0) {
                disc.parent(pattern);
            } else {
                disc.override().predicate(Etched.id("pattern"), value.ordinal()).model(pattern);
            }
        }
        generator.output.accept(ModelLocationUtils.getModelLocation(EtchedItems.ETCHED_MUSIC_DISC), disc::build);
        generator.generateFlatItem(EtchedItems.BOOMBOX, ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(EtchedItems.JUKEBOX_MINECART, ModelTemplates.FLAT_ITEM);
        ModelTemplates.FLAT_ITEM.create(Etched.id("etched_album_cover/blank"), TextureMapping.layer0(Etched.id("item/blank_album_cover")), generator.output);
        ModelTemplates.FLAT_ITEM.create(Etched.id("etched_album_cover/default"), TextureMapping.layer0(Etched.id("item/default_album_cover")), generator.output);
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(EtchedItems.ALBUM_COVER), TextureMapping.layer0(Etched.id("item/default_album_cover")), generator.output);
        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            ResourceLocation name = entry.getKey().location();
            Item item = entry.getValue();
            if (item.components().has(DataComponents.JUKEBOX_PLAYABLE)) {
                ModelTemplates.FLAT_ITEM.create(name.withPath(p -> "etched_album_cover/" + p), TextureMapping.layer0(Etched.id("item/vanilla_album_cover")), generator.output);
            }
        }
//        ItemModelBuilder boomboxPlaying = this.getBuilder("item/boombox_playing")
//                .parent(new ModelFile.ExistingModelFile(Etched.etchedPath("item/boombox_in_hand_playing"), this.existingFileHelper))
//                .customLoader(SeparateTransformsModelBuilder::begin)
//                .base(new ItemModelBuilder(Etched.etchedPath("boombox_in_hand_playing"), this.existingFileHelper)
//                        .parent(new ModelFile.ExistingModelFile(Etched.etchedPath("item/boombox_in_hand_playing"), this.existingFileHelper)))
//                .perspective(ItemDisplayContext.GUI, boomboxInventory)
//                .perspective(ItemDisplayContext.GROUND, boomboxInventory)
//                .perspective(ItemDisplayContext.FIXED, boomboxInventory)
//                .end();
//        this.getBuilder(EtchedItems.BOOMBOX)
//                .parent(new ModelFile.ExistingModelFile(Etched.etchedPath("item/boombox_in_hand"), this.existingFileHelper))
//                .customLoader(SeparateTransformsModelBuilder::begin)
//                .base(new ItemModelBuilder(Etched.etchedPath("boombox_in_hand"), this.existingFileHelper)
//                        .parent(new ModelFile.ExistingModelFile(Etched.etchedPath("item/boombox_in_hand"), this.existingFileHelper)))
//                .perspective(ItemDisplayContext.GUI, boomboxInventory)
//                .perspective(ItemDisplayContext.GROUND, boomboxInventory)
//                .perspective(ItemDisplayContext.FIXED, boomboxInventory)
//                .end()
//                .override()
//                .model(boomboxPlaying)
//                .predicate(Etched.etchedPath("playing"), 1.0F);
        generate(ModelLocationUtils.getModelLocation(EtchedBlocks.ETCHING_TABLE.asItem()), generator.output, builder -> builder.parent(ModelLocationUtils.getModelLocation(EtchedBlocks.ETCHING_TABLE)));
        generate(ModelLocationUtils.getModelLocation(EtchedBlocks.ALBUM_JUKEBOX.asItem()), generator.output, builder -> builder.parent(ModelLocationUtils.getModelLocation(EtchedBlocks.ALBUM_JUKEBOX)));
        generator.generateFlatItem(EtchedBlocks.RADIO.asItem(), ModelTemplates.FLAT_ITEM);
        generator.generateFlatItem(EtchedItems.PORTAL_RADIO, ModelTemplates.FLAT_ITEM);
    }
    public static void generate(ResourceLocation identifier, BiConsumer<ResourceLocation, Supplier<JsonElement>> output, UnaryOperator<ModelBuilder> unary) {
        output.accept(identifier, () -> unary.apply(new ModelBuilder()).build());
    }
    public static class ModelBuilder {
        public JsonObject json = new JsonObject();
        public ModelBuilder parent(ResourceLocation identifier) {
            json.addProperty("parent", identifier.toString());
            return this;
        }
        public Override override() {
            Override override = new Override();
            if (json.has("overrides")) {
                json.getAsJsonArray("overrides").add(override.json);
            } else {
                JsonArray overrides = new JsonArray();
                overrides.add(override.json);
                json.add("overrides", overrides);
            }
            return override;
        }
        public static class Override {
            public JsonObject json = new JsonObject();
            public Override predicate(ResourceLocation identifier, float value){
                JsonObject predicate = new JsonObject();
                predicate.addProperty(identifier.toString(), value);
                json.add("predicate", predicate);
                return this;
            }
            public Override model(ResourceLocation identifier) {
                json.addProperty("model", identifier.toString());
                return this;
            }
        }
        public JsonObject build() {
            return json;
        }
    }
}
