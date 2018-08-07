package me.paulf.wings.client;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.block.WingsBlocks;
import me.paulf.wings.server.item.StandardWing;
import me.paulf.wings.server.item.WingsItems;
import me.paulf.wings.util.Util;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = WingsMod.ID)
public final class WingsModels {
	private WingsModels() {}

	@SubscribeEvent
	public static void register(ModelRegistryEvent event) {
		StandardWing.stream()
			.map(StandardWing::getId)
			.map(Util::requireItem)
			.forEach(WingsModels::register);
		register(WingsBlocks.FAIRY_DUST_ORE);
		register(WingsBlocks.AMETHYST_ORE);
		register(WingsItems.FAIRY_DUST);
		register(WingsItems.AMETHYST);
		register(WingsItems.BAT_BLOOD);
	}

	private static void register(Block block) {
		register(ForgeRegistries.ITEMS.getValue(Util.getName(block)));
	}

	private static void register(Item item) {
		register(item, 0, "inventory");
	}

	private static void register(Item item, int meta, String variant) {
		register(item, meta, Util.getName(item).toString(), variant);
	}

	private static void register(Item item, int meta, String location, String variant) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(location, variant));
	}
}
