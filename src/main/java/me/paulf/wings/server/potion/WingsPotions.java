package me.paulf.wings.server.potion;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.effect.WingsEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;

public class WingsPotions {
    private WingsPotions() {}

    public static final DeferredRegister<Potion> REG = DeferredRegister.create(ForgeRegistries.POTION_TYPES, WingsMod.ID);

    public static final RegistryObject<WingsPotion> ANGEL_WINGS = REG.register("angel_wings", () -> createWingsEffect(0xffffff));

    public static final RegistryObject<WingsPotion> BAT_WINGS = REG.register("bat_wings", () -> createWingsEffect(0xffffff));

    public static final RegistryObject<WingsPotion> BLUE_BUTTERFLY_WINGS = REG.register("blue_butterfly_wings", () -> createWingsEffect(0xffffff));

    public static final RegistryObject<WingsPotion> DRAGON_WINGS = REG.register("dragon_wings", () -> createWingsEffect(0xffffff));

    public static final RegistryObject<WingsPotion> EVIL_WINGS = REG.register("evil_wings", () -> createWingsEffect(0xffffff));

    public static final RegistryObject<WingsPotion> FAIRY_WINGS = REG.register("fairy_wings", () -> createWingsEffect(0xffffff));

    public static final RegistryObject<WingsPotion> FIRE_WINGS = REG.register("fire_wings", () -> createWingsEffect(0xffffff));

    public static final RegistryObject<WingsPotion> MONARCH_BUTTERFLY_WINGS = REG.register("monarch_butterfly_wings", () -> createWingsEffect(0xffffff));

    public static final RegistryObject<WingsPotion> SLIME_WINGS = REG.register("slime_wings", () -> createWingsEffect(0xffffff));

    public static WingsPotion createWingsEffect(int color) {
        EffectInstance effect = new EffectInstance(WingsEffects.WINGS.get(), Integer.MAX_VALUE, 0, true, false);
        effect.setCurativeItems(Collections.singletonList(new ItemStack(Items.APPLE)));
        return new WingsPotion(color, effect);
    }

}
