package gg.moonflower.etched.registry.event;

import gg.moonflower.etched.api.record.PlayableRecord;
import gg.moonflower.etched.registry.component.AlbumCoverComponent;
import gg.moonflower.etched.registry.component.MusicLabelComponent;
import gg.moonflower.etched.registry.component.PausedComponent;
import gg.moonflower.etched.registry.component.PlayingRecordComponent;
import gg.moonflower.etched.registry.component.EtchedComponents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class EtchedClientEvents {
    public static void getTooltip(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, List<Component> components) {
        AlbumCoverComponent albumCoverComponent = itemStack.get(EtchedComponents.ALBUM_COVER);
        if (albumCoverComponent != null) {
            albumCoverComponent.addToTooltip(tooltipContext, components::add, tooltipFlag);
        }
        MusicLabelComponent musicLabelComponent = itemStack.get(EtchedComponents.MUSIC_LABEL);
        if (musicLabelComponent != null) {
            musicLabelComponent.addToTooltip(tooltipContext, components::add, tooltipFlag);
        }
        PlayingRecordComponent playingRecordComponent = itemStack.get(EtchedComponents.PLAYING_RECORD);
        if (playingRecordComponent != null) {
            playingRecordComponent.addToTooltip(tooltipContext, components::add, tooltipFlag);
        }
        PlayableRecord.addToTooltip(itemStack, tooltipContext, components::add);
        PausedComponent pausedComponent = itemStack.get(EtchedComponents.PAUSED);
        if (pausedComponent != null) {
            pausedComponent.addToTooltip(tooltipContext, components::add, tooltipFlag);
        }
    }
    public static void register(){
        ItemTooltipCallback.EVENT.register(EtchedClientEvents::getTooltip);
    }
}
