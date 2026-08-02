package gg.moonflower.etched.client;

import gg.moonflower.etched.client.render.item.AlbumCoverItemRenderer;
import gg.moonflower.etched.registry.item.BoomboxItem;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
public class EtchedModelLoadingPlugin implements ModelLoadingPlugin {
    public static final String PREFIX = "models/";
    public static final String SUFFIX = ".json";
    @Override
    public void onInitializeModelLoader(Context context) {
        for (ResourceLocation location : Minecraft.getInstance().getResourceManager().listResources(PREFIX + AlbumCoverItemRenderer.DIRECTORY, EtchedModelLoadingPlugin::hasSuffix).keySet()) {
            context.addModels(location.withPath(path -> path.substring(PREFIX.length(), path.length() - SUFFIX.length())));
        }
        context.addModels(BoomboxItem.IN_HAND, AlbumCoverItemRenderer.BLANK_ALBUM_COVER.id(), AlbumCoverItemRenderer.DEFAULT_ALBUM_COVER.id());
    }
    public static boolean hasSuffix(ResourceLocation location) {
        return location.getPath().endsWith(SUFFIX);
    }
}