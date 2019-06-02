package me.paulf.wings.server.config;

import me.paulf.wings.WingsMod;
import net.minecraftforge.common.config.Config;

@Config(modid = WingsMod.ID, name = WingsMod.ID + "/wings")
public final class WingsConfig
{
	@Config.LangKey("config.wings.armorblacklist")
	public static String[] disallowedItems = new String[] {
			"minecraft:elytra"
	};
}
