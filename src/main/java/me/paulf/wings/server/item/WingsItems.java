package me.paulf.wings.server.item;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.block.WingsBlocks;
import me.paulf.wings.server.config.ConfigWingSettings;
import me.paulf.wings.server.config.WingsItemsConfig;
import me.paulf.wings.server.item.group.ItemGroupWings;
import me.paulf.wings.util.CapabilityProviders;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class WingsItems {
	private WingsItems() {}

	public static final DeferredRegister<Item> REG = DeferredRegister.create(ForgeRegistries.ITEMS, WingsMod.ID);

	public static final RegistryObject<Item> FAIRY_DUST = REG.register("fairy_dust", () -> new Item(new Item.Properties().group(ItemGroupWings.instance())));

	public static final RegistryObject<Item> BAT_BLOOD = REG.register("bat_blood", () -> new Item(new Item.Properties().containerItem(Items.GLASS_BOTTLE).group(ItemGroupWings.instance())));

	public static final RegistryObject<Item> ANGEL_WINGS = REG.register("angel_wings", () -> createWings(Names.ANGEL, WingsMod.instance()::createAvianWings, WingsItemsConfig.ANGEL));

	public static final RegistryObject<Item> SLIME_WINGS = REG.register("slime_wings", () -> createWings(Names.SLIME, WingsMod.instance()::createInsectoidWings, WingsItemsConfig.SLIME));

	public static final RegistryObject<Item> BLUE_BUTTERFLY_WINGS = REG.register("blue_butterfly_wings", () -> createWings(Names.BLUE_BUTTERFLY, WingsMod.instance()::createInsectoidWings, WingsItemsConfig.BLUE_BUTTERFLY));

	public static final RegistryObject<Item> MONARCH_BUTTERFLY_WINGS = REG.register("monarch_butterfly_wings", () -> createWings(Names.MONARCH_BUTTERFLY, WingsMod.instance()::createInsectoidWings, WingsItemsConfig.MONARCH_BUTTERFLY));

	public static final RegistryObject<Item> FIRE_WINGS = REG.register("fire_wings", () -> createWings(Names.FIRE, WingsMod.instance()::createAvianWings, WingsItemsConfig.FIRE));

	public static final RegistryObject<Item> BAT_WINGS = REG.register("bat_wings", () -> createWings(Names.BAT, WingsMod.instance()::createAvianWings, WingsItemsConfig.BAT));

	public static final RegistryObject<Item> FAIRY_WINGS = REG.register("fairy_wings", () -> createWings(Names.FAIRY, WingsMod.instance()::createInsectoidWings, WingsItemsConfig.FAIRY));

	public static final RegistryObject<Item> EVIL_WINGS = REG.register("evil_wings", () -> createWings(Names.EVIL, WingsMod.instance()::createAvianWings, WingsItemsConfig.EVIL));

	public static final RegistryObject<Item> DRAGON_WINGS = REG.register("dragon_wings", () -> createWings(Names.DRAGON, WingsMod.instance()::createAvianWings, WingsItemsConfig.DRAGON));

	private static Item createWings(final ResourceLocation name, final Function<String, Consumer<CapabilityProviders.CompositeBuilder>> capabilities, final ConfigWingSettings attributes) {
		return ItemWings.create(capabilities.apply(name.getPath().replace("_wings", "")), attributes.toImmutable());
	}

	public static final class Names {
		private Names() {}

		public static final ResourceLocation
			ANGEL = create("angel_wings"),
			SLIME = create("slime_wings"),
			BLUE_BUTTERFLY = create("blue_butterfly_wings"),
			MONARCH_BUTTERFLY = create("monarch_butterfly_wings"),
			FIRE = create("fire_wings"),
			BAT = create("bat_wings"),
			FAIRY = create("fairy_wings"),
			EVIL = create("evil_wings"),
			DRAGON = create("dragon_wings");

		private static ResourceLocation create(final String path) {
			return new ResourceLocation(WingsMod.ID, path);
		}
	}
}
