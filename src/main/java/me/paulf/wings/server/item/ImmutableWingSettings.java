package me.paulf.wings.server.item;

public final class ImmutableWingSettings implements WingSettings {
	private final int requiredFlightSatiation;

	private final float flyingExertion;

	private final int requiredLandSatiation;

	private final float landingExertion;

	private final int itemDurability;

	private ImmutableWingSettings(int requiredFlightSatiation, float flyingExertion, int requiredLandSatiation, float landingExertion, int itemDurability) {
		this.requiredFlightSatiation = requiredFlightSatiation;
		this.flyingExertion = flyingExertion;
		this.requiredLandSatiation = requiredLandSatiation;
		this.landingExertion = landingExertion;
		this.itemDurability = itemDurability;
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

	public static ImmutableWingSettings of(int requiredFlightSatiation, float flyingExertion, int requiredLandSatiation, float landingExertion, int durability) {
		return new ImmutableWingSettings(requiredFlightSatiation, flyingExertion, requiredLandSatiation, landingExertion, durability);
	}
}
