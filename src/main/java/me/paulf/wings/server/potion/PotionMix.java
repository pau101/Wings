package me.paulf.wings.server.potion;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipe;

public class PotionMix extends BrewingRecipe {
    private final Potion from;

    public PotionMix(final Potion from, final Ingredient ingredient, final Potion to) {
        this(from, ingredient, createPotionStack(to));
    }

    public PotionMix(final Potion from, final Ingredient ingredient, final ItemStack result) {
        super(Ingredient.fromStacks(createPotionStack(from)), ingredient, result);
        this.from = from;
    }

    @Override
    public boolean isInput(final ItemStack stack) {
        return !stack.isEmpty() && PotionUtils.getPotionFromItem(stack) == this.from;
    }

    private static ItemStack createPotionStack(final Potion potion) {
        return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), potion);
    }
}
