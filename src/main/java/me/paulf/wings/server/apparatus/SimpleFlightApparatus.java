package me.paulf.wings.server.apparatus;

import me.paulf.wings.server.flight.Flight;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.BiPredicate;
import java.util.function.Function;

public final class SimpleFlightApparatus implements FlightApparatus {
	private final TravelListener flight;

	private final TravelListener landing;

	private final BiPredicate<PlayerEntity, ItemStack> usability;

	private final BiPredicate<PlayerEntity, ItemStack> landability;

	private final Function<Flight, FlightState> vitality;

	private SimpleFlightApparatus(final TravelListener flight, final TravelListener landing, final BiPredicate<PlayerEntity, ItemStack> usability, final BiPredicate<PlayerEntity, ItemStack> landability, final Function<Flight, FlightState> vitality) {
		this.flight = flight;
		this.landing = landing;
		this.usability = usability;
		this.landability = landability;
		this.vitality = vitality;
	}

	@Override
	public void onFlight(final PlayerEntity player, final ItemStack stack, final Vector3d direction) {
		this.flight.onTravel(player, stack, direction);
	}

	@Override
	public void onLanding(final PlayerEntity player, final ItemStack stack, final Vector3d direction) {
		this.landing.onTravel(player, stack, direction);
	}

	@Override
	public boolean isUsable(final PlayerEntity player, final ItemStack stack) {
		return this.usability.test(player, stack);
	}

	@Override
	public boolean isLandable(final PlayerEntity player, final ItemStack stack) {
		return this.landability.test(player, stack);
	}

	@Override
	public FlightState createState(final Flight flight) {
		return this.vitality.apply(flight);
	}

	public static Builder builder() {
		return new Builder();
	}

	@FunctionalInterface
	public interface TravelListener {
		void onTravel(PlayerEntity player, ItemStack wings, Vector3d direction);
	}

	public static final class Builder {
		private TravelListener flight = (p, s, d) -> {};

		private TravelListener landing = (p, s, d) -> {};

		private BiPredicate<PlayerEntity, ItemStack> usability = (p, s) -> true;

		private BiPredicate<PlayerEntity, ItemStack> landability = (p, s) -> true;

		private Function<Flight, FlightState> vitality = f -> FlightState.VOID;

		private Builder() {}

		public Builder withFlight(final TravelListener flight) {
			this.flight = flight;
			return this;
		}

		public Builder withLanding(final TravelListener landing) {
			this.landing = landing;
			return this;
		}

		public Builder withUsability(final BiPredicate<PlayerEntity, ItemStack> usability) {
			this.usability = usability;
			return this;
		}

		public Builder withLandability(final BiPredicate<PlayerEntity, ItemStack> landability) {
			this.landability = landability;
			return this;
		}

		public Builder withVitality(final Function<Flight, FlightState> vitality) {
			this.vitality = vitality;
			return this;
		}

		public SimpleFlightApparatus build() {
			return new SimpleFlightApparatus(this.flight, this.landing, this.usability, this.landability, this.vitality);
		}
	}
}
