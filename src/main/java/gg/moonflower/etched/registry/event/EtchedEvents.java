package gg.moonflower.etched.registry.event;

import gg.moonflower.etched.registry.block.EtchedBlocks;
import gg.moonflower.etched.registry.item.EtchedItems;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class EtchedEvents {
    private static void afterChangeWorld(Entity original, Entity replacement, ServerLevel origin, ServerLevel destination) {
        if (replacement instanceof ItemEntity itemEntity && origin.dimension() != destination.dimension()) {
            ItemStack itemStack = itemEntity.getItem();
            if (itemStack.is(EtchedBlocks.RADIO.asItem())) {
                ItemStack newItemStack = new ItemStack(EtchedItems.PORTAL_RADIO, itemStack.getCount());
                newItemStack.applyComponents(itemStack.getComponents());
                itemEntity.setItem(newItemStack);
            }
        }
    }
    public static void register(){
        ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register(EtchedEvents::afterChangeWorld);
    }
}
