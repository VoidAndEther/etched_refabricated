package gg.moonflower.etched.client.render.item;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.etched.api.record.AlbumCover;
import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.registry.component.AlbumCoverComponent;
import gg.moonflower.etched.Etched;
import gg.moonflower.etched.registry.component.EtchedComponents;
import gg.moonflower.etched.mixin.client.render.SpriteContentsAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.NativeResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Ocelot
 */
public class AlbumCoverItemRenderer implements IdentifiableResourceReloadListener, BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final ResourceLocation IDENTIFIER = Etched.id("reload_listener");
    public static final AlbumCoverItemRenderer INSTANCE = new AlbumCoverItemRenderer();
    public static final String DIRECTORY = Etched.MOD_ID + "_album_cover";

    public static final ModelResourceLocation BLANK_ALBUM_COVER = ModelResourceLocation.inventory(Etched.id(DIRECTORY + "/blank"));
    public static final ModelResourceLocation DEFAULT_ALBUM_COVER = ModelResourceLocation.inventory(Etched.id(DIRECTORY + "/default"));
    private static final ResourceLocation ALBUM_COVER_OVERLAY = Etched.id("textures/item/album_cover_overlay.png");

    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final BlockModel MODEL = BlockModel.fromString("{\"gui_light\":\"front\",\"textures\":{\"layer0\":\"texture\"},\"display\":{\"ground\":{\"rotation\":[0,0,0],\"translation\":[0,2,0],\"scale\":[0.5,0.5,0.5]},\"head\":{\"rotation\":[0,180,0],\"translation\":[0,13,7],\"scale\":[1,1,1]},\"thirdperson_righthand\":{\"rotation\":[0,0,0],\"translation\":[0,3,1],\"scale\":[0.55,0.55,0.55]},\"firstperson_righthand\":{\"rotation\":[0,-90,25],\"translation\":[1.13,3.2,1.13],\"scale\":[0.68,0.68,0.68]},\"fixed\":{\"rotation\":[0,180,0],\"scale\":[1,1,1]}}}");

    private final Map<Integer, CompletableFuture<EtchedModelData>> covers;
    private CoverData data;

    public static void register() {
        ClientPlayConnectionEvents.DISCONNECT.register(AlbumCoverItemRenderer::onPlayDisconnect);
    }

    private static void onPlayDisconnect(ClientPacketListener handler, Minecraft client) {
        INSTANCE.close();
    }

    private AlbumCoverItemRenderer() {
        this.covers = new Int2ObjectArrayMap<>();
        this.data = null;
    }

    public static NativeImage getOverlayImage() {
        return INSTANCE.data.overlay.getImage();
    }

    private static void renderModelLists(BakedModel model, int combinedLight, int combinedOverlay, PoseStack matrixStack, VertexConsumer buffer, RenderType ignored) {
        RandomSource randomsource = RandomSource.create();

        for (Direction direction : Direction.values()) {
            randomsource.setSeed(42L);
            renderQuadList(matrixStack, buffer, model.getQuads(null, direction, randomsource), combinedLight, combinedOverlay);
        }

        randomsource.setSeed(42L);
        renderQuadList(matrixStack, buffer, model.getQuads(null, null, randomsource), combinedLight, combinedOverlay);
    }

    private static void renderQuadList(PoseStack matrixStack, VertexConsumer buffer, List<BakedQuad> quads, int combinedLight, int combinedOverlay) {
        PoseStack.Pose pose = matrixStack.last();
        for (BakedQuad bakedQuad : quads) {
            buffer.putBulkData(pose, bakedQuad, 1, 1, 1, 1, combinedLight, combinedOverlay);
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private static NativeImage getCoverOverlay(ResourceManager resourceManager) {
        try {
            try (InputStream stream = resourceManager.getResourceOrThrow(AlbumCoverItemRenderer.ALBUM_COVER_OVERLAY).open()) {
                return NativeImage.read(stream);
            }
        } catch (IOException e) {
            e.printStackTrace();

            NativeImage nativeImage = new NativeImage(16, 16, false);
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (k < 8 ^ l < 8) {
                        nativeImage.setPixelRGBA(l, k, -524040);
                    } else {
                        nativeImage.setPixelRGBA(l, k, -16777216);
                    }
                }
            }

            nativeImage.untrack();
            return nativeImage;
        }
    }

    private void close() {
        this.covers.values().forEach(future -> future.thenAcceptAsync(data -> {
            if (!this.data.is(data)) {
                data.close();
            }
        }, task -> RenderSystem.recordRenderCall(task::run)));
        this.covers.clear();
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> new CoverData(getCoverOverlay(resourceManager)), backgroundExecutor)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync(data -> {
                    if (this.data != null) {
                        this.data.close();
                    }
                    this.data = data;
                    this.close();
                }, gameExecutor);
    }

    @Override
    public void render(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (stack.isEmpty()) {
            return;
        }

        EtchedModelData model = this.data.defaultCover;
        AlbumCoverComponent albumCover = stack.get(EtchedComponents.ALBUM_COVER);
        if (albumCover != null) {
            ItemStack coverStack = albumCover.getCoverStack();
            if (!coverStack.isEmpty()) {
                model = this.covers.computeIfAbsent(ItemStack.hashItemAndComponents(coverStack), unused -> PlayableRecord.getAlbumCover(
                                coverStack,
                                Minecraft.getInstance().getProxy(),
                                Minecraft.getInstance().getResourceManager())
                        .exceptionally(t -> {
                            Etched.LOGGER.error("Error retrieving album cover", t);
                            return null;
                        })
                        .thenApply(cover -> EtchedModelData.of(cover).orElse(this.data.defaultCover))).getNow(this.data.defaultCover);
            }
        }

        matrices.pushPose();
        matrices.translate(0.5D, 0.5D, 0.5D);
        model.render(stack, mode, matrices, vertexConsumers, light, overlay);
        matrices.popPose();
    }

    @Override
    public ResourceLocation getFabricId() {
        return IDENTIFIER;
    }

    private static class CoverData {

        private final DynamicModelData overlay;
        private final EtchedModelData blank;
        private final EtchedModelData defaultCover;

        private CoverData(NativeImage overlay) {
            this.overlay = new DynamicModelData(overlay);
            this.blank = new BakedModelData(BLANK_ALBUM_COVER);
            this.defaultCover = new BakedModelData(DEFAULT_ALBUM_COVER);
        }

        public void close() {
            this.overlay.close();
            this.blank.close();
            this.defaultCover.close();
        }

        public boolean is(EtchedModelData data) {
            return this.overlay == data || this.blank == data || this.defaultCover == data;
        }
    }

    private static final class BakedModelData implements EtchedModelData {

        private final ModelResourceLocation model;
        private boolean rendering;

        private BakedModelData(ModelResourceLocation model) {
            this.model = model;
        }

        @Override
        public void render(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, int combinedOverlay) {
            ModelManager modelManager = Minecraft.getInstance().getModelManager();
            BakedModel model = this.rendering ? modelManager.getMissingModel() : modelManager.getModel(this.model);
            this.rendering = true; // Prevent deadlock from repeated rendering
            Minecraft.getInstance().getItemRenderer().render(stack, transformType, transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND, matrixStack, buffer, packedLight, combinedOverlay, model);
            this.rendering = false;
        }

        @Override
        public void free() {
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }

            BakedModelData that = (BakedModelData) o;
            return this.model.equals(that.model);
        }

        @Override
        public int hashCode() {
            return this.model.hashCode();
        }
    }

    private static final class DynamicModelData extends TextureAtlasSprite implements EtchedModelData {

        private static final ResourceLocation ATLAS = Etched.id(DigestUtils.md5Hex(UUID.randomUUID().toString()));
        private BakedModel model;

        private DynamicModelData(NativeImage image) {
            super(ATLAS, new SpriteContents(Etched.id(DigestUtils.md5Hex(UUID.randomUUID().toString())), new FrameSize(image.getWidth(), image.getHeight()), image, ResourceMetadata.EMPTY), image.getWidth(), image.getHeight(), 0, 0);
        }

        @Override
        public void render(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, int combinedOverlay) {
            BakedModel model = this.getModel();
            if (model.isCustomRenderer()) {
                return;
            }
            model.getTransforms().getTransform(transformType).apply(transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || transformType == ItemDisplayContext.THIRD_PERSON_LEFT_HAND, matrixStack);
            matrixStack.translate(-0.5D, -0.5D, -0.5D);
            RenderType renderType = RenderType.entityCutout(this.contents().name());
            renderModelLists(model, packedLight, combinedOverlay, matrixStack, ItemRenderer.getFoilBufferDirect(buffer, renderType, false, stack.hasFoil()), renderType);
        }

        @SuppressWarnings("ConstantValue")
        private BakedModel getModel() {
            ResourceLocation name = this.contents().name();
            if (this.model == null) {
                ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
                profiler.push("buildAlbumCoverModel");
                this.model = ITEM_MODEL_GENERATOR.generateBlockModel(material -> this, MODEL).bake(null, MODEL, material -> this, BlockModelRotation.X0_Y0, false);
                profiler.pop();
            }
            TextureManager textureManager = Minecraft.getInstance().getTextureManager();
            if (textureManager.getTexture(name, null) == null) {
                textureManager.register(name, new DynamicTexture(this.getImage()));
            }
            return this.model;
        }

        public NativeImage getImage() {
            return ((SpriteContentsAccessor)this.contents()).getOriginalImage();
        }

        @Override
        public float uvShrinkRatio() {
            return 0.0F;
        }

        @Override
        public @NotNull VertexConsumer wrap(VertexConsumer buffer) {
            return buffer;
        }

        @Override
        public void free() {
            this.contents().close();
            Minecraft.getInstance().getTextureManager().release(this.contents().name());
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }

            DynamicModelData that = (DynamicModelData) o;
            return this.model.equals(that.model);
        }

        @Override
        public int hashCode() {
            return this.model.hashCode();
        }
    }

    @ApiStatus.Internal
    public sealed interface EtchedModelData extends NativeResource {

        static Optional<EtchedModelData> of(AlbumCover cover) {
            if (cover instanceof AlbumCover.ModelAlbumCover(ModelResourceLocation model)) {
                return Optional.of(new BakedModelData(model));
            }
            if (cover instanceof AlbumCover.ImageAlbumCover(NativeImage image)) {
                return Optional.of(new DynamicModelData(image));
            }
            return Optional.empty();
        }

        void render(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, int combinedOverlay);
    }
}
