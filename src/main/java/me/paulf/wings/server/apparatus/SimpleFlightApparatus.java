package me.paulf.wings.server.apparatus;

import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.item.WingSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Objects;

public final class SimpleFlightApparatus implements FlightApparatus {
	private final WingSettings settings;

	public SimpleFlightApparatus(final WingSettings settings) {
		this.settings = Objects.requireNonNull(settings);
	}

	@Override
	public void onFlight(final PlayerEntity player, final ItemStack stack, final Vector3d direction) {
		int distance = Math.round((float) direction.length() * 100.0F);
		if (distance > 0) {
			player.addExhaustion(distance * this.settings.getFlyingExertion());
		}
	}

	@Override
	public void onLanding(final PlayerEntity player, final ItemStack stack, final Vector3d direction) {
		player.addExhaustion(this.settings.getLandingExertion());
	}

	@Override
	public boolean isUsable(final PlayerEntity player) {
		return player.getFoodStats().getFoodLevel() >= this.settings.getRequiredFlightSatiation();
	}

	@Override
	public boolean isLandable(final PlayerEntity player, final ItemStack stack) {
		return player.getFoodStats().getFoodLevel() >= this.settings.getRequiredLandSatiation();
	}

	@Override
	public FlightState createState(final Flight flight) {
		return (player, stack) -> {};
	}
}
