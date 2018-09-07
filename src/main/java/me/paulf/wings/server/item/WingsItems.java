package me.paulf.wings.server.item;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.block.WingsBlocks;
import me.paulf.wings.server.item.group.ItemGroupWings;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.Reg;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
			createWings("angel", 120, WingsMod.instance()::createAvianWings),
			createWings("slime", 180, WingsMod.instance()::createInsectoidWings),
			createWings("blue_butterfly", 120, WingsMod.instance()::createInsectoidWings),
			createWings("monarch_butterfly", 120, WingsMod.instance()::createInsectoidWings),
			createWings("fire", 180, WingsMod.instance()::createAvianWings),
			createWings("bat", 180, WingsMod.instance()::createAvianWings),
			createWings("fairy", 120, WingsMod.instance()::createInsectoidWings),
			createWings("evil", 340, WingsMod.instance()::createAvianWings),
			createWings("dragon", 360, WingsMod.instance()::createAvianWings)
		);
	}

	private static Item createWings(String name, int durability, Function<String, Consumer<CapabilityProviders.CompositeBuilder>> capabilities) {
		return Reg.withName(ItemWings.create(durability, capabilities.apply(name)), String.format("%s_wings", name))
			.setCreativeTab(ItemGroupWings.instance());
	}
}
