package gg.moonflower.etched.registry.item;

import gg.moonflower.etched.registry.block.EtchedBlocks;
import gg.moonflower.etched.registry.component.DiscAppearanceComponent;
import gg.moonflower.etched.registry.component.MusicLabelComponent;
import gg.moonflower.etched.Etched;
import gg.moonflower.etched.registry.component.EtchedComponents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class EtchedItems {
    public static final MusicLabelItem MUSIC_LABEL = register("music_label", MusicLabelItem::new,  settings -> settings.component(EtchedComponents.MUSIC_LABEL, MusicLabelComponent.DEFAULT));
    public static final Item BLANK_MUSIC_DISC = register("blank_music_disc", Item::new);
    public static final Item ETCHED_MUSIC_DISC = register("etched_music_disc", Item::new,settings -> settings.component(EtchedComponents.DISC_APPEARANCE, DiscAppearanceComponent.DEFAULT).stacksTo(1));
    public static final MinecartJukeboxItem JUKEBOX_MINECART = register("jukebox_minecart", MinecartJukeboxItem::new, settings -> settings.stacksTo(1));
    public static final BoomboxItem BOOMBOX = register("boombox", BoomboxItem::new, settings -> settings.stacksTo(1));
    public static final AlbumCoverItem ALBUM_COVER = register("album_cover", AlbumCoverItem::new, settings -> settings.stacksTo(1));
    public static final PortalRadioItem PORTAL_RADIO = register("portal_radio", settings -> new PortalRadioItem(EtchedBlocks.RADIO, settings));

    public static <T extends Item> T register(String name, Function<Properties, T> constructor, UnaryOperator<Properties> unary) {
        return Registry.register(BuiltInRegistries.ITEM, Etched.id(name), constructor.apply(unary.apply(new Item.Properties())));
    }
    public static <T extends Item> T register(String name, Function<Properties, T> constructor) {
        return Registry.register(BuiltInRegistries.ITEM, Etched.id(name), constructor.apply(new Properties()));
    }
    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(EtchedItems::addToolsAndUtilities);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(EtchedItems::addRedstoneBlocks);
        Map<Item, CauldronInteraction> map = CauldronInteraction.WATER.map();
        map.put(EtchedItems.BLANK_MUSIC_DISC, CauldronInteraction.DYED_ITEM);
        map.put(EtchedItems.MUSIC_LABEL, MusicLabelComponent::dyedMusicLabel);
    }
    private static void addToolsAndUtilities(FabricItemGroupEntries entries) {
        entries.addBefore(Items.MUSIC_DISC_13, EtchedItems.MUSIC_LABEL, EtchedItems.BLANK_MUSIC_DISC);
        entries.addAfter(Items.MUSIC_DISC_PIGSTEP, EtchedItems.ALBUM_COVER, EtchedItems.BOOMBOX);
    }

    private static void addRedstoneBlocks(FabricItemGroupEntries entries) {
        entries.addAfter(Items.TNT_MINECART, EtchedItems.JUKEBOX_MINECART);
    }
}
