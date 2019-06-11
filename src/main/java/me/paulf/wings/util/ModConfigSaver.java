package me.paulf.wings.util;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ModConfigSaver {
	private final String id;

	private ModConfigSaver(final String id) {
		this.id = id;
	}

	@SubscribeEvent
	public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
		if (this.id.equals(event.getModID())) {
			ConfigManager.sync(this.id, Config.Type.INSTANCE);
		}
	}

	public static ModConfigSaver create(final String id) {
		return new ModConfigSaver(id);
	}
}
