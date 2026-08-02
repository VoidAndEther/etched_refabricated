package gg.moonflower.etched.registry.recipe;

import gg.moonflower.etched.registry.item.EtchedItems;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MusicDiscCloningRecipe extends CustomRecipe {

    public MusicDiscCloningRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        ItemStack base = ItemStack.EMPTY;
        ItemStack copy = ItemStack.EMPTY;

        for (int j = 0; j < inv.size(); ++j) {
            ItemStack stack = inv.getItem(j);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(ConventionalItemTags.MUSIC_DISCS)) {
                if (!base.isEmpty()) {
                    return false;
                }

                base = stack;
            } else {
                if (!copy.isEmpty()) {
                    return false;
                }
                if (!stack.is(EtchedItems.BLANK_MUSIC_DISC)) {
                    return false;
                }

                copy = stack;
            }
        }

        return !base.isEmpty() && !copy.isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput container, HolderLookup.Provider registryAccess) {
        ItemStack base = ItemStack.EMPTY;
        ItemStack copy = ItemStack.EMPTY;

        for (int j = 0; j < container.size(); ++j) {
            ItemStack stack = container.getItem(j);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(ConventionalItemTags.MUSIC_DISCS)) {
                if (!base.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                base = stack;
            } else {
                if (!copy.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                if (!stack.is(EtchedItems.BLANK_MUSIC_DISC)) {
                    return ItemStack.EMPTY;
                }

                copy = stack;
            }
        }

        if (base.isEmpty() || copy.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return base.copyWithCount(1);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.size(), ItemStack.EMPTY);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            Item craftingRemainingItem = stack.getItem().getCraftingRemainingItem();
            if (craftingRemainingItem != null) {
                list.set(i, new ItemStack(craftingRemainingItem));
            } else if (!stack.is(EtchedItems.BLANK_MUSIC_DISC)) {
                list.set(i, stack.copyWithCount(1));
            }
        }

        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EtchedRecipes.CLONE_MUSIC_DISC;
    }
}
