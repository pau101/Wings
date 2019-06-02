package me.paulf.wings.server.config;

import me.paulf.wings.WingsMod;
import net.minecraftforge.common.config.Config;

@Config(modid = WingsMod.ID, name = WingsMod.ID)
public final class WingsConfig
{
	@Config.LangKey("config.wings.wearObstructions")
	public static String[] wearObstructions = new String[] {
			"minecraft:elytra"
	};
}
