package me.paulf.wings.server.config;

import me.paulf.wings.server.item.ImmutableWingSettings;
import me.paulf.wings.server.item.WingSettings;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("DeprecatedIsStillUsed")
public final class ConfigWingSettings implements WingSettings {
    private final ResourceLocation key;

    @Deprecated
//	@Config.LangKey("config.wings.items.settings.requiredFlightSatiation") FIXME: config
//	@Config.RangeInt(min = 0, max = 20)
    public int requiredFlightSatiation;

    @Deprecated
//	@Config.LangKey("config.wings.items.settings.flyingExertion")
//	@Config.RangeDouble(min = 0.0D, max = 10.0D)
    public double flyingExertion;

    @Deprecated
//	@Config.LangKey("config.wings.items.settings.requiredLandSatiation")
//	@Config.RangeInt(min = 0, max = 20)
    public int requiredLandSatiation;

    @Deprecated
//	@Config.LangKey("config.wings.items.settings.landingExertion")
//	@Config.RangeDouble(min = 0.0D, max = 10.0D)
    public double landingExertion;

    ConfigWingSettings(ResourceLocation key) {
        this(key, 7, 0.001D, 2, 0.08D);
    }

    private ConfigWingSettings(ResourceLocation key, int requiredFlightSatiation, double flyingExertion, int requiredLandSatiation, double landingExertion) {
        this.key = key;
        this.requiredFlightSatiation = requiredFlightSatiation;
        this.flyingExertion = flyingExertion;
        this.requiredLandSatiation = requiredLandSatiation;
        this.landingExertion = landingExertion;
    }

    public ResourceLocation getKey() {
        return this.key;
    }

    @Override
    public int getRequiredFlightSatiation() {
        return this.requiredFlightSatiation;
    }

    @Override
    public float getFlyingExertion() {
        return (float) this.flyingExertion;
    }

    @Override
    public int getRequiredLandSatiation() {
        return this.requiredLandSatiation;
    }

    @Override
    public float getLandingExertion() {
        return (float) this.landingExertion;
    }

    public WingSettings toImmutable() {
        return ImmutableWingSettings.of(this.getRequiredFlightSatiation(), this.getFlyingExertion(), this.getRequiredLandSatiation(), this.getLandingExertion());
    }
}
