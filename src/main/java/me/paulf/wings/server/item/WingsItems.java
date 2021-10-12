package me.paulf.wings.server.item;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.apparatus.FlightApparatus;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class WingsItems {
    private WingsItems() {
    }

    public static final DeferredRegister<Item> REG = DeferredRegister.create(ForgeRegistries.ITEMS, WingsMod.ID);

    public static final RegistryObject<Item> BAT_BLOOD_BOTTLE = REG.register("bat_blood_bottle",
        () -> new BatBloodBottleItem(new Item.Properties()
            .craftRemainder(Items.GLASS_BOTTLE)
            .tab(ItemGroup.TAB_FOOD)
            .stacksTo(16)));

    public static final RegistryObject<Item> ANGEL_WINGS_BOTTLE = REG.register("angel_wings_bottle", bottle(() -> WingsMod.ANGEL_WINGS));
    public static final RegistryObject<Item> SLIME_WINGS_BOTTLE = REG.register("slime_wings_bottle", bottle(() -> WingsMod.SLIME_WINGS));
    public static final RegistryObject<Item> BLUE_BUTTERFLY_WINGS_BOTTLE = REG.register("blue_butterfly_wings_bottle", bottle(() -> WingsMod.BLUE_BUTTERFLY_WINGS));
    public static final RegistryObject<Item> MONARCH_BUTTERFLY_WINGS_BOTTLE = REG.register("monarch_butterfly_wings_bottle", bottle(() -> WingsMod.MONARCH_BUTTERFLY_WINGS));
    public static final RegistryObject<Item> FIRE_WINGS_BOTTLE = REG.register("fire_wings_bottle", bottle(() -> WingsMod.FIRE_WINGS));
    public static final RegistryObject<Item> BAT_WINGS_BOTTLE = REG.register("bat_wings_bottle", bottle(() -> WingsMod.BAT_WINGS));
    public static final RegistryObject<Item> FAIRY_WINGS_BOTTLE = REG.register("fairy_wings_bottle", bottle(() -> WingsMod.FAIRY_WINGS));
    public static final RegistryObject<Item> EVIL_WINGS_BOTTLE = REG.register("evil_wings_bottle", bottle(() -> WingsMod.EVIL_WINGS));
    public static final RegistryObject<Item> DRAGON_WINGS_BOTTLE = REG.register("dragon_wings_bottle", bottle(() -> WingsMod.DRAGON_WINGS));

    private static Supplier<Item> bottle(Supplier<FlightApparatus> wings) {
        return () -> new WingsBottleItem(new Item.Properties()
            .craftRemainder(Items.GLASS_BOTTLE)
            .tab(ItemGroup.TAB_TRANSPORTATION)
            .stacksTo(16), wings.get());
    }

}
