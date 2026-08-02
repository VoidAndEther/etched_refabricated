package gg.moonflower.etched.registry.item;

import gg.moonflower.etched.client.screen.EditMusicLabelScreen;
import gg.moonflower.etched.registry.component.MusicLabelComponent;
import gg.moonflower.etched.registry.component.EtchedComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MusicLabelItem extends Item {

    public MusicLabelItem(Properties properties) {
        super(properties);
    }

    @Environment(EnvType.CLIENT)
    private void openMusicLabelEditScreen(Player player, InteractionHand hand, ItemStack stack) {
        Minecraft.getInstance().setScreen(new EditMusicLabelScreen(player, hand, stack));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            this.openMusicLabelEditScreen(player, hand, stack);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide()) {
            MusicLabelComponent label = stack.getOrDefault(EtchedComponents.MUSIC_LABEL, MusicLabelComponent.DEFAULT);
            if (label.artist().isEmpty()) {
                stack.set(EtchedComponents.MUSIC_LABEL, label.withArtist(entity.getDisplayName().getString()));
            }
        }
    }

    public static int getColor(ItemStack stack, int index) {
        return index == 0 || index == 1 ? DyedItemColor.getOrDefault(stack, -1) : -1;
    }

    public static int getBlankColor(ItemStack stack, int index) {
        return index > 0 ? -1 : 0xFF000000 | DyedItemColor.getOrDefault(stack, 0x515151);
    }

//    @Override
//    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
//        if (!getAuthor(itemStack).isEmpty() && !getTitle(itemStack).isEmpty()) {
//            list.add(Component.translatable("sound_source." + Etched.MOD_ID + ".info", getAuthor(itemStack), getTitle(itemStack)).withStyle(ChatFormatting.GRAY));
//        }
//    }
}
