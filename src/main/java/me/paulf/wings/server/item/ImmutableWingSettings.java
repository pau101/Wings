package me.paulf.wings.server.item;

public final class ImmutableWingSettings implements WingSettings {
	private final int requiredFlightSatiation;

	private final float flyingExertion;

	private final int requiredLandSatiation;

	private final float landingExertion;

	private final int itemDurability;

	private final boolean alternativeMode;

	private ImmutableWingSettings(int requiredFlightSatiation, float flyingExertion, int requiredLandSatiation, float landingExertion, int itemDurability, boolean alternativeMode) {
		this.requiredFlightSatiation = requiredFlightSatiation;
		this.flyingExertion = flyingExertion;
		this.requiredLandSatiation = requiredLandSatiation;
		this.landingExertion = landingExertion;
		this.itemDurability = itemDurability;
		this.alternativeMode = alternativeMode;
	}

	@Override
	public int getRequiredFlightSatiation() {
		return requiredFlightSatiation;
	}

	@Override
	public float getFlyingExertion() {
		return flyingExertion;
	}

	@Override
	public int getRequiredLandSatiation() {
		return requiredLandSatiation;
	}

	@Override
	public float getLandingExertion() {
		return landingExertion;
	}

	@Override
	public int getItemDurability() {
		return itemDurability;
	}

	@Override
	public boolean getAlternativeMode() {
		return alternativeMode;
	}

	public static ImmutableWingSettings of(int requiredFlightSatiation, float flyingExertion, int requiredLandSatiation, float landingExertion, int durability, boolean alternativeMode) {
		return new ImmutableWingSettings(requiredFlightSatiation, flyingExertion, requiredLandSatiation, landingExertion, durability, alternativeMode);
	}
}
