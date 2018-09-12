package me.paulf.wings.server.item;

public final class ImmutableWingSettings implements WingSettings {
	private final int requiredFlightSatiation;

	private final float flyingExertion;

	private final int requiredLandSatiation;

	private final float landingExertion;

	private ImmutableWingSettings(int requiredFlightSatiation, float flyingExertion, int requiredLandSatiation, float landingExertion) {
		this.requiredFlightSatiation = requiredFlightSatiation;
		this.flyingExertion = flyingExertion;
		this.requiredLandSatiation = requiredLandSatiation;
		this.landingExertion = landingExertion;
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

	public static ImmutableWingSettings of(int requiredFlightSatiation, float flyingExertion, int requiredLandSatiation, float landingExertion) {
		return new ImmutableWingSettings(requiredFlightSatiation, flyingExertion, requiredLandSatiation, landingExertion);
	}
}
