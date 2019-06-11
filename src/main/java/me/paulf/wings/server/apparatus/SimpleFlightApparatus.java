package me.paulf.wings.server.apparatus;

import me.paulf.wings.server.flight.Flight;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiPredicate;
import java.util.function.Function;

public final class SimpleFlightApparatus implements FlightApparatus {
	private final TravelListener flight;

	private final TravelListener landing;

	private final BiPredicate<EntityPlayer, ItemStack> usability;

	private final BiPredicate<EntityPlayer, ItemStack> landability;

	private final Function<Flight, FlightState> vitality;

	private SimpleFlightApparatus(final TravelListener flight, final TravelListener landing, final BiPredicate<EntityPlayer, ItemStack> usability, final BiPredicate<EntityPlayer, ItemStack> landability, final Function<Flight, FlightState> vitality) {
		this.flight = flight;
		this.landing = landing;
		this.usability = usability;
		this.landability = landability;
		this.vitality = vitality;
	}

	@Override
	public void onFlight(final EntityPlayer player, final ItemStack stack, final Vec3d direction) {
		this.flight.onTravel(player, stack, direction);
	}

	@Override
	public void onLanding(final EntityPlayer player, final ItemStack stack, final Vec3d direction) {
		this.landing.onTravel(player, stack, direction);
	}

	@Override
	public boolean isUsable(final EntityPlayer player, final ItemStack stack) {
		return this.usability.test(player, stack);
	}

	@Override
	public boolean isLandable(final EntityPlayer player, final ItemStack stack) {
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
		void onTravel(EntityPlayer player, ItemStack wings, Vec3d direction);
	}

	public static final class Builder {
		private TravelListener flight = (p, s, d) -> {};

		private TravelListener landing = (p, s, d) -> {};

		private BiPredicate<EntityPlayer, ItemStack> usability = (p, s) -> true;

		private BiPredicate<EntityPlayer, ItemStack> landability = (p, s) -> true;

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

		public Builder withUsability(final BiPredicate<EntityPlayer, ItemStack> usability) {
			this.usability = usability;
			return this;
		}

		public Builder withLandability(final BiPredicate<EntityPlayer, ItemStack> landability) {
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
