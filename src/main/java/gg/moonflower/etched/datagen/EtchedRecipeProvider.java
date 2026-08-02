package gg.moonflower.etched.datagen;

import gg.moonflower.etched.registry.recipe.MusicDiscCloningRecipe;
import gg.moonflower.etched.registry.recipe.MusicLabelDyeRecipe;
import gg.moonflower.etched.registry.recipe.MusicLabelMergeRecipe;
import gg.moonflower.etched.Etched;
import gg.moonflower.etched.registry.block.EtchedBlocks;
import gg.moonflower.etched.registry.item.EtchedItems;
import gg.moonflower.etched.util.EtchedConventionalItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;

import java.util.concurrent.CompletableFuture;

public class EtchedRecipeProvider extends FabricRecipeProvider {


    public EtchedRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EtchedItems.MUSIC_LABEL)
                .define('P', EtchedConventionalItemTags.PAPER)
                .pattern(" P ")
                .pattern("P P")
                .pattern(" P ")
                .unlockedBy("has_paper", has(EtchedConventionalItemTags.PAPER))
                .save(recipeOutput);
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(ConventionalItemTags.MUSIC_DISCS),
                RecipeCategory.TOOLS,
                EtchedItems.BLANK_MUSIC_DISC,
                0.2F,
                200)
                .unlockedBy("has_music_disc", has(ConventionalItemTags.MUSIC_DISCS))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, EtchedItems.JUKEBOX_MINECART)
                .requires(Blocks.JUKEBOX)
                .requires(Items.MINECART)
                .unlockedBy("has_minecart", has(Items.MINECART))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EtchedItems.BOOMBOX)
                .define('I', ConventionalItemTags.COPPER_INGOTS)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('J', Blocks.JUKEBOX)
                .pattern(" I ")
                .pattern("IRI")
                .pattern("IJI")
                .unlockedBy("has_jukebox", has(Blocks.JUKEBOX))
                .unlockedBy("has_music_disc", has(ConventionalItemTags.MUSIC_DISCS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EtchedItems.ALBUM_COVER)
                .define('P', EtchedConventionalItemTags.PAPER)
                .define('M', EtchedItems.MUSIC_LABEL)
                .pattern("PPP")
                .pattern("PMP")
                .pattern("PPP")
                .unlockedBy("has_etched_music_disc", has(EtchedItems.ETCHED_MUSIC_DISC))
                .unlockedBy("has_music_label", has(EtchedItems.MUSIC_LABEL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EtchedBlocks.ETCHING_TABLE)
                .define('D', ConventionalItemTags.DIAMOND_GEMS)
                .define('I', ConventionalItemTags.IRON_INGOTS)
                .define('P', ItemTags.PLANKS)
                .pattern(" DI")
                .pattern("PPP")
                .unlockedBy("has_jukebox", has(Blocks.JUKEBOX))
                .unlockedBy("has_blank_music_disc", has(EtchedItems.BLANK_MUSIC_DISC))
                .unlockedBy("has_music_label", has(EtchedItems.MUSIC_LABEL))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EtchedBlocks.ALBUM_JUKEBOX)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('H', Items.REPEATER)
                .define('J', Blocks.JUKEBOX)
                .define('C', ConventionalItemTags.WOODEN_CHESTS)
                .pattern("RHR")
                .pattern("RJR")
                .pattern("RCR")
                .unlockedBy("has_jukebox", has(Blocks.JUKEBOX))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, EtchedBlocks.RADIO)
                .define('R', ConventionalItemTags.REDSTONE_DUSTS)
                .define('C', Items.COPPER_INGOT)
                .define('I', ConventionalItemTags.IRON_INGOTS)
                .define('N', Blocks.NOTE_BLOCK)
                .define('P', ItemTags.PLANKS)
                .pattern("RCR")
                .pattern("INI")
                .pattern("PPP")
                .unlockedBy("has_note_block", has(Blocks.NOTE_BLOCK))
                .unlockedBy("has_jukebox", has(Blocks.JUKEBOX))
                .unlockedBy("has_music_disc", has(ConventionalItemTags.MUSIC_DISCS))
                .save(recipeOutput);

        SpecialRecipeBuilder.special(MusicLabelMergeRecipe::new).save(recipeOutput, Etched.id("merge_music_label"));
        SpecialRecipeBuilder.special(MusicDiscCloningRecipe::new).save(recipeOutput, Etched.id("music_disc_cloning"));
        SpecialRecipeBuilder.special(MusicLabelDyeRecipe::new).save(recipeOutput, Etched.id("dye_music_label"));
    }
}
