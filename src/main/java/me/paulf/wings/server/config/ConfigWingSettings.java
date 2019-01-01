package me.paulf.wings.server.config;

import me.paulf.wings.server.item.ImmutableWingSettings;
import me.paulf.wings.server.item.WingSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;

@SuppressWarnings("DeprecatedIsStillUsed")
public final class ConfigWingSettings implements WingSettings {
	private final ResourceLocation key;

	@Deprecated
	@Config.LangKey("config.wings.items.settings.requiredFlightSatiation")
	@Config.RangeInt(min = 0, max = 20)
	public int requiredFlightSatiation;

	@Deprecated
	@Config.LangKey("config.wings.items.settings.flyingExertion")
	@Config.RangeDouble(min = 0.0D, max = 10.0D)
	public double flyingExertion;

	@Deprecated
	@Config.LangKey("config.wings.items.settings.requiredLandSatiation")
	@Config.RangeInt(min = 0, max = 20)
	public int requiredLandSatiation;

	@Deprecated
	@Config.LangKey("config.wings.items.settings.landingExertion")
	@Config.RangeDouble(min = 0.0D, max = 10.0D)
	public double landingExertion;

	@Deprecated
	@Config.LangKey("config.wings.items.settings.durability")
	@Config.RangeInt(min = 0, max = Short.MAX_VALUE)
	public int itemDurability;

	//added an alternative mode which drains minerals instead of food
	@Deprecated
	@Config.LangKey("config.wings.items.settings.alternativeMode")
	public boolean alternativeMode;

	ConfigWingSettings(ResourceLocation key, int itemDurability) {
		this(key, 7, 0.001D, 2, 0.08D, itemDurability, false);
	}

	private ConfigWingSettings(ResourceLocation key, int requiredFlightSatiation, double flyingExertion, int requiredLandSatiation, double landingExertion, int itemDurability, boolean alternativeMode) {
		this.key = key;
		this.requiredFlightSatiation = requiredFlightSatiation;
		this.flyingExertion = flyingExertion;
		this.requiredLandSatiation = requiredLandSatiation;
		this.landingExertion = landingExertion;
		this.itemDurability = itemDurability;
		this.alternativeMode = alternativeMode;
	}

	public ResourceLocation getKey() {
		return key;
	}

	@Override
	public int getRequiredFlightSatiation() {
		return requiredFlightSatiation;
	}

	@Override
	public float getFlyingExertion() {
		return (float) flyingExertion;
	}

	@Override
	public int getRequiredLandSatiation() {
		return requiredLandSatiation;
	}

	@Override
	public float getLandingExertion() {
		return (float) landingExertion;
	}

	@Override
	public int getItemDurability() {
		return itemDurability;
	}

	@Override
	public boolean getAlternativeMode() {
		return alternativeMode;
	}

	public WingSettings toImmutable() {
		return ImmutableWingSettings.of(getRequiredFlightSatiation(), getFlyingExertion(), getRequiredLandSatiation(), getLandingExertion(), getItemDurability(), getAlternativeMode());
	}
}
