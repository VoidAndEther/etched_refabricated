package gg.moonflower.etched.registry.item;

import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.api.sound.SoundTracker;
import gg.moonflower.etched.registry.component.PausedComponent;
import gg.moonflower.etched.registry.component.PlayingRecordComponent;
import gg.moonflower.etched.registry.menu.BoomboxMenu;
import gg.moonflower.etched.Etched;
import gg.moonflower.etched.EtchedConfig;
import gg.moonflower.etched.registry.component.EtchedComponents;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class BoomboxItem extends Item implements ContainerItem {
    public static final ResourceLocation IN_HAND = Etched.id("item/boombox_in_hand");
    private static final Map<Integer, ItemStack> PLAYING_RECORDS = new Int2ObjectArrayMap<>();
    private static final Component PAUSE = Component.translatable("item." + Etched.MOD_ID + ".boombox.pause", Component.keybind("key.sneak"), Component.keybind("key.use")).withStyle(ChatFormatting.GRAY);

    public BoomboxItem(Properties properties) {
        super(properties);
    }

    public static ItemStack findPlayingRecord(Player player) {
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            PlayingRecordComponent component = stack.get(EtchedComponents.PLAYING_RECORD);
            if (component != null && !stack.has(EtchedComponents.PAUSED)) {
                return component.stack();
            }
        }
        if (player == Minecraft.getInstance().cameraEntity) {
            ItemStack carried = player.inventoryMenu.getCarried();
            PlayingRecordComponent component = carried.get(EtchedComponents.PLAYING_RECORD);
            if (component != null && !carried.has(EtchedComponents.PAUSED)) {
                return component.stack();
            }
            for (ItemStack stack : player.getInventory().items) {
                if (stack.has(EtchedComponents.PLAYING_RECORD) && !stack.has(EtchedComponents.PAUSED)) {
                    return BoomboxItem.getRecord(stack);
                }
            }
        }
        return ItemStack.EMPTY;
    }
    public static void updatePlaying(int id, ItemStack record) {
        if (!ItemStack.matches(PLAYING_RECORDS.getOrDefault(id, ItemStack.EMPTY), record)) {
            SoundTracker.playBoombox(id, record);
            if (record.isEmpty()) {
                PLAYING_RECORDS.remove(id);
            } else {
                PLAYING_RECORDS.put(id, record);
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isSecondaryUseActive()) {
            if (stack.has(EtchedComponents.PAUSED)) {
                stack.remove(EtchedComponents.PAUSED);
            } else {
                stack.set(EtchedComponents.PAUSED, PausedComponent.INSTANCE);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        if (!EtchedConfig.INSTANCE.useBoomboxMenu()) {
            return InteractionResultHolder.fail(stack);
        }
        return this.use(this, level, player, hand);
    }

    @Override
    public AbstractContainerMenu constructMenu(int containerId, Inventory inventory, Player player, int index) {
        return new BoomboxMenu(containerId, inventory, index);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack boombox, Slot slot, ClickAction clickAction, Player player) {
        if (EtchedConfig.INSTANCE.useBoomboxMenu() || clickAction != ClickAction.SECONDARY) {
            return false;
        }
        ItemStack clickItem = slot.getItem();
        if (clickItem.isEmpty()) {
            ItemStack record = getRecord(boombox);
            if (!record.isEmpty()) {
                player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                setRecord(boombox, slot.safeInsert(record));
                return true;
            }
        } else if (PlayableRecord.isPlayableRecord(clickItem)) {
            player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
            ItemStack old = getRecord(boombox);
            setRecord(boombox, slot.safeTake(clickItem.getCount(), 1, player).split(1));
            slot.set(old);
            return true;
        }

        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack boombox, ItemStack clickItem, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        if (EtchedConfig.INSTANCE.useBoomboxMenu()) {
            return false;
        }
        if (clickAction != ClickAction.SECONDARY) {
            return false;
        }

        if (slot.allowModification(player)) {
            if (clickItem.isEmpty()) {
                ItemStack record = getRecord(boombox);
                if (!record.isEmpty()) {
                    player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                    slotAccess.set(record);
                    setRecord(boombox, ItemStack.EMPTY);
                    return true;
                }
            } else if (PlayableRecord.isPlayableRecord(clickItem)) {
                ItemStack old = getRecord(boombox);
                if (old.isEmpty() || clickItem.getCount() == 1) {
                    player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
                    setRecord(boombox, clickItem.split(1));
                    slotAccess.set(old);
                    return true;
                }
            }
        }

        return false;
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(PAUSE);
    }

    /**
     * Retrieves the current hand boombox sounds are coming from for the specified entity.
     *
     * @param entity The entity to check
     * @return The hand the entity is using or <code>null</code> if no boombox is playing
     */
    public static @Nullable InteractionHand getPlayingHand(LivingEntity entity) {
        if (!PLAYING_RECORDS.containsKey(entity.getId())) {
            return null;
        }
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = entity.getItemInHand(hand);
            if (hasRecord(stack) && !stack.has(EtchedComponents.PAUSED)) {
                return hand;
            }
        }
        return null;
    }

    public static float getPlayingHandIndex(ItemStack stack, ClientLevel ignoredLevel, LivingEntity entity, int ignoredIndex) {
        InteractionHand hand = entity != null ? BoomboxItem.getPlayingHand(entity) : null;
        return hand != null && stack == entity.getItemInHand(hand) ? 1 : 0;
    }

    public static boolean hasRecord(ItemStack stack) {
        return stack.has(EtchedComponents.PLAYING_RECORD);
    }

    public static ItemStack getRecord(ItemStack stack) {
        PlayingRecordComponent record = stack.get(EtchedComponents.PLAYING_RECORD);
        return record != null ? record.stack() : ItemStack.EMPTY;
    }

    public static void setRecord(ItemStack stack, ItemStack record) {
        if (record.isEmpty()) {
            stack.remove(EtchedComponents.PLAYING_RECORD);
        } else {
            stack.set(EtchedComponents.PLAYING_RECORD, new PlayingRecordComponent(record.copyWithCount(1)));
        }
    }
}
