package gg.moonflower.etched;

import gg.moonflower.etched.client.AlbumCoverCache;
import gg.moonflower.etched.client.EtchedModelLoadingPlugin;
import gg.moonflower.etched.client.render.EtchedModelLayers;
import gg.moonflower.etched.client.render.JukeboxMinecartRenderer;
import gg.moonflower.etched.client.render.item.AlbumCoverItemRenderer;
import gg.moonflower.etched.client.screen.*;
import gg.moonflower.etched.registry.block.EtchedBlocks;
import gg.moonflower.etched.registry.component.DiscAppearanceComponent;
import gg.moonflower.etched.registry.component.MusicLabelComponent;
import gg.moonflower.etched.registry.item.BoomboxItem;
import gg.moonflower.etched.registry.component.EtchedComponents;
import gg.moonflower.etched.registry.entity.EtchedEntities;
import gg.moonflower.etched.registry.item.EtchedItems;
import gg.moonflower.etched.registry.item.MusicLabelItem;
import gg.moonflower.etched.registry.menu.EtchedMenuTypes;
import gg.moonflower.etched.registry.event.EtchedClientEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.DyedItemColor;

public class EtchedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AlbumCoverCache.register();
        MenuScreens.register(EtchedMenuTypes.ETCHING_MENU, EtchingScreen::new);
        MenuScreens.register(EtchedMenuTypes.ALBUM_JUKEBOX_MENU, AlbumJukeboxScreen::new);
        MenuScreens.register(EtchedMenuTypes.BOOMBOX_MENU, BoomboxScreen::new);
        MenuScreens.register(EtchedMenuTypes.ALBUM_COVER_MENU, AlbumCoverScreen::new);
        MenuScreens.register(EtchedMenuTypes.RADIO_MENU, RadioScreen::new);

        EntityRendererRegistry.register(EtchedEntities.JUKEBOX_MINECART, JukeboxMinecartRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(EtchedModelLayers.JUKEBOX_MINECART, MinecartModel::createBodyLayer);

        BlockRenderLayerMap.INSTANCE.putBlock(EtchedBlocks.RADIO, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(EtchedBlocks.ETCHING_TABLE, RenderType.cutout());
        ColorProviderRegistry.ITEM.register(MusicLabelItem::getColor);
        ColorProviderRegistry.ITEM.register(MusicLabelItem::getBlankColor, EtchedItems.BLANK_MUSIC_DISC);
        ColorProviderRegistry.ITEM.register(MusicLabelComponent::getColor, EtchedItems.MUSIC_LABEL);
        ColorProviderRegistry.ITEM.register(DiscAppearanceComponent::getColor, EtchedItems.ETCHED_MUSIC_DISC);

        ItemProperties.register(EtchedItems.BOOMBOX, Etched.id("playing"), BoomboxItem::getPlayingHandIndex);
        ItemProperties.register(EtchedItems.ETCHED_MUSIC_DISC, Etched.id("pattern"), DiscAppearanceComponent::getOrdinal);

        ModelLoadingPlugin.register(new EtchedModelLoadingPlugin());
        AlbumCoverItemRenderer.register();
        BuiltinItemRendererRegistry.INSTANCE.register(EtchedItems.ALBUM_COVER, AlbumCoverItemRenderer.INSTANCE);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(AlbumCoverItemRenderer.INSTANCE);

        EtchedClientEvents.register();
        EtchedClientConfig.INSTANCE = EtchedClientConfig.deserialize();
    }
}
