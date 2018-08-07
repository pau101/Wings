package me.paulf.wings.server.config;

import me.paulf.wings.WingsMod;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class WingsConfig {
	private WingsConfig() {}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (WingsMod.ID.equals(event.getModID())) {
			ConfigManager.sync(WingsMod.ID, Config.Type.INSTANCE);
		}
	}
}
