package me.paulf.wings.server.potion;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;

public final class WingsPotion extends Potion {
    private final int color;

    public WingsPotion(int color, EffectInstance... effects) {
        super(effects);
        this.color = color;
    }

    public ItemStack createStack() {
        ItemStack stack = new ItemStack(Items.POTION);
        PotionUtils.setPotion(stack, this);
        stack.getOrCreateTag().putInt("CustomPotionColor", this.color);
        return stack;
    }
}
