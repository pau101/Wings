package me.paulf.wings.client;

import me.paulf.wings.WingsMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = WingsMod.ID)
public final class WingsModels {
	private WingsModels() {}

	@SubscribeEvent
	public static void onRegister(final ModelRegistryEvent event) {
	}
}
