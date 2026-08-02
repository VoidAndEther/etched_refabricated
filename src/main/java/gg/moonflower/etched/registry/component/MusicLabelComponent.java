package gg.moonflower.etched.registry.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.etched.Etched;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;
import java.util.function.Consumer;

public record MusicLabelComponent(String artist, String title, int primaryColor, int secondaryColor) implements TooltipProvider {

    public static final MusicLabelComponent DEFAULT = new MusicLabelComponent("", "Custom Music", -1, -1);

    private static final Codec<MusicLabelComponent> COMPLEX_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("author").forGetter(MusicLabelComponent::artist),
            Codec.STRING.fieldOf("title").forGetter(MusicLabelComponent::title),
            Codec.INT.fieldOf("primaryColor").forGetter(MusicLabelComponent::primaryColor),
            Codec.INT.fieldOf("secondaryColor").forGetter(MusicLabelComponent::secondaryColor)
    ).apply(instance, MusicLabelComponent::new));
    private static final Codec<MusicLabelComponent> SIMPLE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("author").forGetter(MusicLabelComponent::artist),
            Codec.STRING.fieldOf("title").forGetter(MusicLabelComponent::title),
            Codec.INT.fieldOf("color").forGetter(MusicLabelComponent::primaryColor)
    ).apply(instance, MusicLabelComponent::new));
    public static final Codec<MusicLabelComponent> CODEC = Codec.withAlternative(SIMPLE_CODEC, COMPLEX_CODEC);
    public static final StreamCodec<FriendlyByteBuf, MusicLabelComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            MusicLabelComponent::artist,
            ByteBufCodecs.STRING_UTF8,
            MusicLabelComponent::title,
            ByteBufCodecs.INT,
            MusicLabelComponent::primaryColor,
            ByteBufCodecs.INT,
            MusicLabelComponent::secondaryColor,
            MusicLabelComponent::new);

    public MusicLabelComponent(String artist, String title, int primaryColor, int secondaryColor) {
        this.artist = artist;
        this.title = title;
        this.primaryColor = 0xFFFFFF & primaryColor;
        this.secondaryColor = 0xFFFFFF & secondaryColor;
    }

    public MusicLabelComponent(String author, String title, int color) {
        this(author, title, color, color);
    }

    @Override
    public int primaryColor() {
        return 0xFF000000 | this.primaryColor;
    }

    @Override
    public int secondaryColor() {
        return 0xFF000000 | this.secondaryColor;
    }

    public boolean simple() {
        return this.primaryColor == this.secondaryColor;
    }

    public boolean isColored() {
        return this.primaryColor != 0xFFFFFF || this.secondaryColor != 0xFFFFFF;
    }

    public MusicLabelComponent withArtist(String author) {
        return new MusicLabelComponent(author, this.title, this.primaryColor, this.secondaryColor);
    }

    public MusicLabelComponent withTitle(String title) {
        return new MusicLabelComponent(this.artist, title, this.primaryColor, this.secondaryColor);
    }

    public MusicLabelComponent withInfo(String artist, String title) {
        return new MusicLabelComponent(artist, title, this.primaryColor, this.secondaryColor);
    }

    public MusicLabelComponent withColor(int color) {
        return new MusicLabelComponent(this.artist, this.title, color, color);
    }

    public MusicLabelComponent withColor(int primaryColor, int secondaryColor) {
        return new MusicLabelComponent(this.artist, this.title, primaryColor, secondaryColor);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        if (!this.artist.isEmpty() && !this.title.isEmpty()) {
            tooltipAdder.accept(Component.translatable("sound_source." + Etched.MOD_ID + ".info", this.artist, this.title).withStyle(ChatFormatting.GRAY));
        }
        if (this.primaryColor != 0xFFFFFF || this.secondaryColor != 0xFFFFFF) {
            if (tooltipFlag.isAdvanced()) {
                if (this.simple()) {
                    tooltipAdder.accept(Component.translatable("item.color", String.format(Locale.ROOT, "#%06X", this.primaryColor)).withStyle(ChatFormatting.GRAY));
                } else {
                    tooltipAdder.accept(Component.translatable("item.color", String.format(Locale.ROOT, "#%06X", this.primaryColor)).withStyle(ChatFormatting.GRAY));
                    tooltipAdder.accept(Component.translatable("item.color", String.format(Locale.ROOT, "#%06X", this.secondaryColor)).withStyle(ChatFormatting.GRAY));
                }
            } else {
                tooltipAdder.accept(Component.translatable("item.dyed").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }
    }
    public static ItemInteractionResult dyedMusicLabel(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, ItemStack itemStack) {
        MusicLabelComponent label = itemStack.get(EtchedComponents.MUSIC_LABEL);
        if (label == null || !label.isColored()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!level.isClientSide()) {
            ItemStack newStack = itemStack.copy();
            newStack.set(EtchedComponents.MUSIC_LABEL, label.withColor(-1, -1));
            player.setItemInHand(interactionHand, newStack);
            player.awardStat(Stats.CLEAN_ARMOR);
            LayeredCauldronBlock.lowerFillLevel(blockState, level, blockPos);
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    public static int getColor(ItemStack stack, int index) {
        MusicLabelComponent label = stack.getOrDefault(EtchedComponents.MUSIC_LABEL, MusicLabelComponent.DEFAULT);
        if (index == 0) {
            return label.primaryColor();
        }
        if (index == 1) {
            return label.secondaryColor();
        }
        return -1;
    }
}
