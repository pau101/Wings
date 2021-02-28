package me.paulf.wings.server.config;

@SuppressWarnings("DeprecatedIsStillUsed")
public final class VeinSettings {
	@Deprecated
//	@Config.LangKey("config.wings.ores.settings.size") FIXME: config
//	@Config.RangeInt(min = 8, max = 32)
	public int size;

	@Deprecated
//	@Config.LangKey("config.wings.ores.settings.count")
//	@Config.RangeInt(min = 0, max = 128)
	public int count;

	@Deprecated
//	@Config.LangKey("config.wings.ores.settings.minHeight")
	public int minHeight;

	@Deprecated
//	@Config.LangKey("config.wings.ores.settings.maxHeight")
	public int maxHeight;

	VeinSettings(final int size, final int count, final int minHeight, final int maxHeight) {
		this.size = size;
		this.count = count;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
	}

	public int getSize() {
		return this.size;
	}

	public int getCount() {
		return this.count;
	}

	public int getMinHeight() {
		return this.minHeight;
	}

	public int getMaxHeight() {
		return this.maxHeight;
	}
}
