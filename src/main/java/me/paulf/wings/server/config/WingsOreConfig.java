package me.paulf.wings.server.config;

//@Config(modid = WingsMod.ID, name = WingsMod.ID + "/ores", category = "") FIXME: config
public final class WingsOreConfig {
	private WingsOreConfig() {}

//	@Config.LangKey("config.wings.ores.entry.fairy_dust")
//	@Config.RequiresMcRestart
	public static final VeinSettings FAIRY_DUST = new VeinSettings(9, 10, 0, 64);

//	@Config.LangKey("config.wings.ores.entry.amethyst")
//	@Config.RequiresMcRestart
	public static final VeinSettings AMETHYST = new VeinSettings(8, 1, 0, 16);
}
