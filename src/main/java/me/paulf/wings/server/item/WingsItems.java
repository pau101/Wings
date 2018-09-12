package me.paulf.wings.server.item;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.block.WingsBlocks;
import me.paulf.wings.server.config.ConfigWingSettings;
import me.paulf.wings.server.config.WingsItemsConfig;
import me.paulf.wings.server.item.group.ItemGroupWings;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.Reg;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.function.Consumer;
import java.util.function.Function;

@GameRegistry.ObjectHolder(WingsMod.ID)
@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class WingsItems {
	private WingsItems() {}

	public static final Item FAIRY_DUST = Items.AIR;

	public static final Item AMETHYST = Items.AIR;

	public static final Item BAT_BLOOD = Items.AIR;

	public static final Item ANGEL_WINGS = Items.AIR;

	public static final Item SLIME_WINGS = Items.AIR;

	public static final Item BLUE_BUTTERFLY_WINGS = Items.AIR;

	public static final Item MONARCH_BUTTERFLY_WINGS = Items.AIR;

	public static final Item FIRE_WINGS = Items.AIR;

	public static final Item BAT_WINGS = Items.AIR;

	public static final Item FAIRY_WINGS = Items.AIR;

	public static final Item EVIL_WINGS = Items.AIR;

	public static final Item DRAGON_WINGS = Items.AIR;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		final int easiest = 960, moderate = 1920, hardest = 2880;
		event.getRegistry().registerAll(
			Reg.createItem(WingsBlocks.FAIRY_DUST_ORE),
			Reg.createItem(WingsBlocks.AMETHYST_ORE),
			Reg.withName(new Item()
				.setCreativeTab(ItemGroupWings.instance()), "fairy_dust"
			),
			Reg.withName(new Item()
				.setCreativeTab(ItemGroupWings.instance()), "amethyst"
			),
			Reg.withName(new Item()
				.setCreativeTab(ItemGroupWings.instance())
				.setContainerItem(Items.GLASS_BOTTLE), "bat_blood"
			),
			createWings(Names.ANGEL, easiest, WingsMod.instance()::createAvianWings, WingsItemsConfig.ANGEL),
			createWings(Names.SLIME, easiest, WingsMod.instance()::createInsectoidWings, WingsItemsConfig.SLIME),
			createWings(Names.BLUE_BUTTERFLY, easiest, WingsMod.instance()::createInsectoidWings, WingsItemsConfig.BLUE_BUTTERFLY),
			createWings(Names.MONARCH_BUTTERFLY, easiest, WingsMod.instance()::createInsectoidWings, WingsItemsConfig.MONARCH_BUTTERFLY),
			createWings(Names.FIRE, moderate, WingsMod.instance()::createAvianWings, WingsItemsConfig.FIRE),
			createWings(Names.BAT, moderate, WingsMod.instance()::createAvianWings, WingsItemsConfig.BAT),
			createWings(Names.FAIRY, easiest, WingsMod.instance()::createInsectoidWings, WingsItemsConfig.FAIRY),
			createWings(Names.EVIL, moderate, WingsMod.instance()::createAvianWings, WingsItemsConfig.EVIL),
			createWings(Names.DRAGON, hardest, WingsMod.instance()::createAvianWings, WingsItemsConfig.DRAGON)
		);
	}

	private static Item createWings(ResourceLocation name, int durability, Function<String, Consumer<CapabilityProviders.CompositeBuilder>> capabilities, ConfigWingSettings attributes) {
		return Reg.withName(
				ItemWings.create(durability, capabilities.apply(name.getPath().replace("_wings", "")), attributes.toImmutable()),
				name.getPath()
			)
			.setCreativeTab(ItemGroupWings.instance());
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

		private static ResourceLocation create(String path) {
			return new ResourceLocation(WingsMod.ID, path);
		}
	}
}
