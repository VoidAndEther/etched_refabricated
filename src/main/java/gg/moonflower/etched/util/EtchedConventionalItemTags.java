package gg.moonflower.etched.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class EtchedConventionalItemTags {
    public static final TagKey<Item> PAPER = register("paper");
    @SuppressWarnings("SameParameterValue")
    private static TagKey<Item> register(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
    }
}
