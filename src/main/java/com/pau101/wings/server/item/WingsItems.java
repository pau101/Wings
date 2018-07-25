package com.pau101.wings.server.item;

import com.pau101.wings.WingsMod;
import com.pau101.wings.server.block.WingsBlocks;
import com.pau101.wings.server.item.group.ItemGroupWings;
import com.pau101.wings.util.Reg;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(WingsMod.ID)
@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class WingsItems {
	private WingsItems() {}

	private static final Item NIL = Items.AIR;

	public static final ItemWings WINGS = ItemWings.nil();

	public static final Item FAIRY_DUST = NIL;

	public static final Item AMETHYST = NIL;

	public static final Item BAT_BLOOD = NIL;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
			Reg.asItem(WingsBlocks.FAIRY_DUST_ORE),
			Reg.asItem(WingsBlocks.AMETHYST_ORE),
			Reg.withName(ItemWings.create()
				.setCreativeTab(ItemGroupWings.instance()), "wings"
			),
			Reg.withName(new Item()
				.setCreativeTab(ItemGroupWings.instance()), "fairy_dust"
			),
			Reg.withName(new Item()
				.setCreativeTab(ItemGroupWings.instance()), "amethyst"
			),
			Reg.withName(new Item()
				.setCreativeTab(ItemGroupWings.instance())
				.setContainerItem(Items.GLASS_BOTTLE),"bat_blood"
			)
		);
	}
}
