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
	public static void register(final RegistryEvent.Register<Item> event) {
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
			createWings(Names.ANGEL, WingsMod.instance()::createAvianWings, WingsItemsConfig.ANGEL),
			createWings(Names.SLIME, WingsMod.instance()::createInsectoidWings, WingsItemsConfig.SLIME),
			createWings(Names.BLUE_BUTTERFLY, WingsMod.instance()::createInsectoidWings, WingsItemsConfig.BLUE_BUTTERFLY),
			createWings(Names.MONARCH_BUTTERFLY, WingsMod.instance()::createInsectoidWings, WingsItemsConfig.MONARCH_BUTTERFLY),
			createWings(Names.FIRE, WingsMod.instance()::createAvianWings, WingsItemsConfig.FIRE),
			createWings(Names.BAT, WingsMod.instance()::createAvianWings, WingsItemsConfig.BAT),
			createWings(Names.FAIRY, WingsMod.instance()::createInsectoidWings, WingsItemsConfig.FAIRY),
			createWings(Names.EVIL, WingsMod.instance()::createAvianWings, WingsItemsConfig.EVIL),
			createWings(Names.DRAGON, WingsMod.instance()::createAvianWings, WingsItemsConfig.DRAGON)
		);
	}

	private static Item createWings(final ResourceLocation name, final Function<String, Consumer<CapabilityProviders.CompositeBuilder>> capabilities, final ConfigWingSettings attributes) {
		return Reg.withName(
				ItemWings.create(capabilities.apply(name.getPath().replace("_wings", "")), attributes.toImmutable()),
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

		private static ResourceLocation create(final String path) {
			return new ResourceLocation(WingsMod.ID, path);
		}
	}
}
