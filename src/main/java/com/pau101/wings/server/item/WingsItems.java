package com.pau101.wings.server.item;

import com.pau101.wings.WingsMod;
import com.pau101.wings.server.block.WingsBlocks;
import com.pau101.wings.server.item.group.ItemGroupWings;
import com.pau101.wings.util.Util;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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
			asItem(WingsBlocks.FAIRY_DUST_ORE),
			asItem(WingsBlocks.AMETHYST_ORE),
			Util.withName(ItemWings.create()
				.setCreativeTab(ItemGroupWings.instance()), "wings"
			),
			Util.withName(new Item()
				.setCreativeTab(ItemGroupWings.instance()), "fairy_dust"
			),
			Util.withName(new Item()
				.setCreativeTab(ItemGroupWings.instance()), "amethyst"
			),
			Util.withName(new Item()
				.setCreativeTab(ItemGroupWings.instance())
				.setContainerItem(Items.GLASS_BOTTLE), "bat_blood"
			)
		);
	}

	private static Item asItem(Block block) {
		return new ItemBlock(block).setRegistryName(Util.getName(block));
	}
}
