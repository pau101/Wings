package me.paulf.wings.client;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.block.WingsBlocks;
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
	public static void onRegister(ModelRegistryEvent event) {
		bindAll(WingsItems.ANGEL_WINGS);
		bindAll(WingsItems.SLIME_WINGS);
		bindAll(WingsItems.BLUE_BUTTERFLY_WINGS);
		bindAll(WingsItems.MONARCH_BUTTERFLY_WINGS);
		bindAll(WingsItems.FIRE_WINGS);
		bindAll(WingsItems.BAT_WINGS);
		bindAll(WingsItems.FAIRY_WINGS);
		bindAll(WingsItems.EVIL_WINGS);
		bindAll(WingsItems.DRAGON_WINGS);
		bindOne(WingsBlocks.FAIRY_DUST_ORE);
		bindOne(WingsBlocks.AMETHYST_ORE);
		bindOne(WingsItems.FAIRY_DUST);
		bindOne(WingsItems.AMETHYST);
		bindOne(WingsItems.BAT_BLOOD);
	}

	private static void bindOne(Block block) {
		bindOne(ForgeRegistries.ITEMS.getValue(Util.getName(block)));
	}

	private static void bindAll(Item item) {
		ModelResourceLocation location = createInventoryLocation(item);
		ModelLoader.setCustomMeshDefinition(item, stack -> location);
	}

	private static void bindOne(Item item) {
		bind(item, 0, createInventoryLocation(item));
	}

	private static void bind(Item item, int meta, ModelResourceLocation location) {
		ModelLoader.setCustomModelResourceLocation(item, meta, location);
	}

	private static ModelResourceLocation createInventoryLocation(Item item) {
		return new ModelResourceLocation(Util.getName(item), "inventory");
	}
}
