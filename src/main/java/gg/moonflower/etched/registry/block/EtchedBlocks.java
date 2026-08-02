package gg.moonflower.etched.registry.block;

import gg.moonflower.etched.Etched;
import gg.moonflower.etched.registry.item.EtchedItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class EtchedBlocks {

    public static final Block ETCHING_TABLE = registerPair("etching_table", EtchingTableBlock::new, null, settings -> settings.mapColor(MapColor.PODZOL).strength(2.5F).sound(SoundType.WOOD));
    public static final Block ALBUM_JUKEBOX = registerPair("album_jukebox", AlbumJukeboxBlock::new, Blocks.JUKEBOX);
    public static final Block RADIO = registerPair("radio", RadioBlock::new, Blocks.JUKEBOX, Properties::noOcclusion);


    public static <T extends Block, S extends BlockBehaviour> T register(String name, Function<Properties, T> constructor, S source, UnaryOperator<Properties> unary) {
        return Registry.register(BuiltInRegistries.BLOCK, Etched.id(name), constructor.apply(unary.apply(source == null ? Properties.of(): Properties.ofFullCopy(source))));
    }
    public static <T extends Block, S extends BlockBehaviour> T registerPair(String name, Function<Properties, T> constructor, S source, UnaryOperator<Properties> unary) {
        T block = register(name, constructor, source, unary);
        EtchedItems.register(name, settings -> new BlockItem(block, settings));
        return block;
    }
    public static <T extends Block, S extends BlockBehaviour> T registerPair(String name, Function<Properties, T> constructor, S source) {
        return registerPair(name, constructor, source, settings -> settings);
    }
    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(EtchedBlocks::addFunctionalBlocks);
    }

    private static void addFunctionalBlocks(FabricItemGroupEntries entries) {
        entries.addAfter(Items.JUKEBOX, EtchedBlocks.ALBUM_JUKEBOX, EtchedBlocks.ETCHING_TABLE, EtchedBlocks.RADIO);
    }
}
