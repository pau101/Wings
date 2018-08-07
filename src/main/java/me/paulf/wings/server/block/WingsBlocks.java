package me.paulf.wings.server.block;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.item.WingsItems;
import me.paulf.wings.server.item.group.ItemGroupWings;
import me.paulf.wings.util.HarvestLevel;
import me.paulf.wings.util.Reg;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(WingsMod.ID)
@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class WingsBlocks {
	private WingsBlocks() {}

	public static final Block FAIRY_DUST_ORE = Blocks.AIR;

	public static final Block AMETHYST_ORE = Blocks.AIR;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(
			Reg.withName(BlockWingsOre.create(() -> WingsItems.FAIRY_DUST, 0, 2, HarvestLevel.STONE)
				.setCreativeTab(ItemGroupWings.instance()),
				"fairy_dust_ore"
			),
			Reg.withName(BlockWingsOre.create(() -> WingsItems.AMETHYST, 3, 7, HarvestLevel.IRON)
				.setCreativeTab(ItemGroupWings.instance()),
				"amethyst_ore"
			)
		);
	}
}
