package gg.moonflower.etched.registry.recipe;

import gg.moonflower.etched.Etched;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class EtchedRecipes {

    public static final SimpleCraftingRecipeSerializer<MusicLabelMergeRecipe> COMPLEX_MUSIC_LABEL = register("merge_music_label", new SimpleCraftingRecipeSerializer<>(MusicLabelMergeRecipe::new));
    public static final SimpleCraftingRecipeSerializer<MusicDiscCloningRecipe> CLONE_MUSIC_DISC = register("music_disc_cloning", new SimpleCraftingRecipeSerializer<>(MusicDiscCloningRecipe::new));
    public static final SimpleCraftingRecipeSerializer<MusicLabelDyeRecipe> DYE_MUSIC_LABEL = register("dye_music_label", new SimpleCraftingRecipeSerializer<>(MusicLabelDyeRecipe::new));
    public static <T extends Recipe<?>, S extends RecipeSerializer<T>> S register(String name, S serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Etched.id(name), serializer);
    }

    public static void register() {
    }
}