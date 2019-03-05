package me.paulf.wings.server.config;

import net.minecraftforge.common.config.Config;

@SuppressWarnings("DeprecatedIsStillUsed")
public final class VeinSettings {
	@Deprecated
	@Config.LangKey("config.wings.ores.settings.size")
	@Config.RangeInt(min = 8, max = 32)
	int size;

	@Deprecated
	@Config.LangKey("config.wings.ores.settings.count")
	@Config.RangeInt(min = 0, max = 128)
	int count;

	@Deprecated
	@Config.LangKey("config.wings.ores.settings.minHeight")
	int minHeight;

	@Deprecated
	@Config.LangKey("config.wings.ores.settings.maxHeight")
	int maxHeight;

	VeinSettings(int size, int count, int minHeight, int maxHeight) {
		this.size = size;
		this.count = count;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
	}

	public int getSize() {
		return size;
	}

	public int getCount() {
		return count;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public int getMaxHeight() {
		return maxHeight;
	}
}
