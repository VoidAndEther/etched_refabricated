package gg.moonflower.etched.registry.menu;

import gg.moonflower.etched.Etched;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class EtchedMenuTypes {
    public static final MenuType<EtchingMenu> ETCHING_MENU = register("etching_table_onmonmonvwmw", EtchingMenu::new);
    public static final MenuType<AlbumJukeboxMenu> ALBUM_JUKEBOX_MENU = register("album_jukebox", AlbumJukeboxMenu::new, BlockPos.STREAM_CODEC);
    public static final MenuType<BoomboxMenu> BOOMBOX_MENU = register("boombox", BoomboxMenu::new);
    public static final MenuType<AlbumCoverMenu> ALBUM_COVER_MENU = register("album_cover", AlbumCoverMenu::new);
    public static final MenuType<RadioMenu> RADIO_MENU = register("radio", RadioMenu::new, ByteBufCodecs.STRING_UTF8);

    public static <T extends AbstractContainerMenu> MenuType<T> register(String name,  MenuType.MenuSupplier<T> supplier) {
        return Registry.register(BuiltInRegistries.MENU, Etched.id(name), new MenuType<>(supplier, FeatureFlagSet.of()));
    }
    public static <T extends AbstractContainerMenu, D> MenuType<T> register(String name, ExtendedScreenHandlerType.ExtendedFactory<T, D> supplier, StreamCodec<? super RegistryFriendlyByteBuf, D> codec) {
        return Registry.register(BuiltInRegistries.MENU, Etched.id(name), new ExtendedScreenHandlerType<>(supplier, codec));
    }

    public static void register() {
    }
}
