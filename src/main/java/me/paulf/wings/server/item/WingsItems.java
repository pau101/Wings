package me.paulf.wings.server.item;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.block.WingsBlocks;
import me.paulf.wings.server.item.group.ItemGroupWings;
import me.paulf.wings.util.Reg;
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

	public static final Item FAIRY_DUST = Items.AIR;

	public static final Item AMETHYST = Items.AIR;

	public static final Item BAT_BLOOD = Items.AIR;

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
				.setContainerItem(Items.GLASS_BOTTLE),"bat_blood"
			)
		);
		StandardWing.stream()
			.map(type -> Reg.withName(ItemWings.create(type)
				.setCreativeTab(ItemGroupWings.instance()), type.getId().getPath()
			))
			.forEach(event.getRegistry()::register);
	}
}
