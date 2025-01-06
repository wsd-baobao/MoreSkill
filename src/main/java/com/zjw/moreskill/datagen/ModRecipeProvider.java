package com.zjw.moreskill.datagen;

import com.zjw.moreskill.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS,ModItems.ForgingHammerItem.get())
                .pattern(" S ")
                .pattern(" I ")
                .pattern(" D ")
                .define('I', Items.STICK)
                .define('S',Items.IRON_BLOCK)
                .define('D',Items.DIAMOND)
                .unlockedBy("has_diamond", has(Items.DIAMOND))
                .save(consumer);
    }
}
