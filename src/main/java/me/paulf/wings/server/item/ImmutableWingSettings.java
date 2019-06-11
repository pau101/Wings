package me.paulf.wings.server.item;

public final class ImmutableWingSettings implements WingSettings {
	private final int requiredFlightSatiation;

	private final float flyingExertion;

	private final int requiredLandSatiation;

	private final float landingExertion;

	private final int itemDurability;

	private ImmutableWingSettings(final int requiredFlightSatiation, final float flyingExertion, final int requiredLandSatiation, final float landingExertion, final int itemDurability) {
		this.requiredFlightSatiation = requiredFlightSatiation;
		this.flyingExertion = flyingExertion;
		this.requiredLandSatiation = requiredLandSatiation;
		this.landingExertion = landingExertion;
		this.itemDurability = itemDurability;
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

	@Override
	public int getItemDurability() {
		return this.itemDurability;
	}

	public static ImmutableWingSettings of(final int requiredFlightSatiation, final float flyingExertion, final int requiredLandSatiation, final float landingExertion, final int durability) {
		return new ImmutableWingSettings(requiredFlightSatiation, flyingExertion, requiredLandSatiation, landingExertion, durability);
	}
}
