package me.paulf.wings.server.item;

public final class ImmutableWingSettings implements WingSettings {
	private final int requiredFlightSatiation;

	private final float flyingExertion;

	private final int requiredLandSatiation;

	private final float landingExertion;

	private ImmutableWingSettings(final int requiredFlightSatiation, final float flyingExertion, final int requiredLandSatiation, final float landingExertion) {
		this.requiredFlightSatiation = requiredFlightSatiation;
		this.flyingExertion = flyingExertion;
		this.requiredLandSatiation = requiredLandSatiation;
		this.landingExertion = landingExertion;
	}

	@Override
	public int getRequiredFlightSatiation() {
		return this.requiredFlightSatiation;
	}

	@Override
	public float getFlyingExertion() {
		return this.flyingExertion;
	}

	@Override
	public int getRequiredLandSatiation() {
		return this.requiredLandSatiation;
	}

	@Override
	public float getLandingExertion() {
		return this.landingExertion;
	}

	public static ImmutableWingSettings of(final int requiredFlightSatiation, final float flyingExertion, final int requiredLandSatiation, final float landingExertion) {
		return new ImmutableWingSettings(requiredFlightSatiation, flyingExertion, requiredLandSatiation, landingExertion);
	}
}
