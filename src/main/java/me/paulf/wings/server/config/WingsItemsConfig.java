package me.paulf.wings.server.config;

import me.paulf.wings.WingsMod;
import net.minecraftforge.common.config.Config;

@Config(modid = WingsMod.ID, name = WingsMod.ID + "/items", category = "")
public class WingsItemsConfig {
	private WingsItemsConfig() {}

	@Config.LangKey("config.wings.items.durability")
	public static final DurabilitySet DURABILITY = new DurabilitySet();

	@SuppressWarnings("DeprecatedIsStillUsed")
	public static final class DurabilitySet {
		private DurabilitySet() {}

		private static final int EASIEST = 960;

		private static final int MODERATE = 1920;

		private static final int HARDEST = 2880;

		@Deprecated
		@Config.LangKey("config.wings.items.durability.angel")
		@Config.RangeInt(min = 0, max = Short.MAX_VALUE)
		@Config.RequiresMcRestart
		public int angel = EASIEST;

		@Deprecated
		@Config.LangKey("config.wings.items.durability.slime")
		@Config.RangeInt(min = 0, max = Short.MAX_VALUE)
		@Config.RequiresMcRestart
		public int slime = EASIEST;

		@Deprecated
		@Config.LangKey("config.wings.items.durability.blue_butterfly")
		@Config.RangeInt(min = 0, max = Short.MAX_VALUE)
		@Config.RequiresMcRestart
		public int blueButterfly = EASIEST;

		@Deprecated
		@Config.LangKey("config.wings.items.durability.monarch_butterfly")
		@Config.RangeInt(min = 0, max = Short.MAX_VALUE)
		@Config.RequiresMcRestart
		public int monarchButterfly = EASIEST;

		@Deprecated
		@Config.LangKey("config.wings.items.durability.fire")
		@Config.RangeInt(min = 0, max = Short.MAX_VALUE)
		@Config.RequiresMcRestart
		public int fire = MODERATE;

		@Deprecated
		@Config.LangKey("config.wings.items.durability.bat")
		@Config.RangeInt(min = 0, max = Short.MAX_VALUE)
		@Config.RequiresMcRestart
		public int bat = MODERATE;

		@Deprecated
		@Config.LangKey("config.wings.items.durability.fairy")
		@Config.RangeInt(min = 0, max = Short.MAX_VALUE)
		@Config.RequiresMcRestart
		public int fairy = EASIEST;

		@Deprecated
		@Config.LangKey("config.wings.items.durability.evil")
		@Config.RangeInt(min = 0, max = Short.MAX_VALUE)
		@Config.RequiresMcRestart
		public int evil = MODERATE;

		@Deprecated
		@Config.LangKey("config.wings.items.durability.dragon")
		@Config.RangeInt(min = 0, max = Short.MAX_VALUE)
		@Config.RequiresMcRestart
		public int dragon = HARDEST;

		public int angel() {
			return angel;
		}

		public int slime() {
			return slime;
		}

		public int blueButterfly() {
			return blueButterfly;
		}

		public int monarchButterfly() {
			return monarchButterfly;
		}

		public int fire() {
			return fire;
		}

		public int bat() {
			return bat;
		}

		public int fairy() {
			return fairy;
		}

		public int evil() {
			return evil;
		}

		public int dragon() {
			return dragon;
		}
	}
}
