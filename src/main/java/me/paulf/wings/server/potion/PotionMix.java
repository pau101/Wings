package me.paulf.wings.server.potion;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipe;

public class PotionMix extends BrewingRecipe {
    private final Potion from;

    public PotionMix(Potion from, Ingredient ingredient, Potion to) {
        this(from, ingredient, createPotionStack(to));
    }

    public PotionMix(Potion from, Ingredient ingredient, ItemStack result) {
        super(Ingredient.of(createPotionStack(from)), ingredient, result);
        this.from = from;
    }

    @Override
    public boolean isInput(ItemStack stack) {
        return !stack.isEmpty() && PotionUtils.getPotion(stack) == this.from;
    }

    private static ItemStack createPotionStack(Potion potion) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), potion);
    }
}
