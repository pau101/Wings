package me.paulf.wings.server.winged;

import me.paulf.wings.server.flight.Flight;
import net.minecraft.item.ItemStack;

import java.util.function.Function;
import java.util.function.Predicate;

public final class SimpleFlightApparatus implements FlightApparatus {
	private final Predicate<ItemStack> usability;

	private final Function<Flight, FlightState> vitality;

	private SimpleFlightApparatus(Predicate<ItemStack> usability, Function<Flight, FlightState> vitality) {
		this.usability = usability;
		this.vitality = vitality;
	}

	@Override
	public boolean isUsable(ItemStack stack) {
		return usability.test(stack);
	}

	@Override
	public FlightState createState(Flight flight) {
		return vitality.apply(flight);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Predicate<ItemStack> usability = s -> true;

		private Function<Flight, FlightState> vitality = f -> FlightState.VOID;

		private Builder() {}

		public Builder withUsability(Predicate<ItemStack> usability) {
			this.usability = usability;
			return this;
		}

		public Builder withVitality(Function<Flight, FlightState> vitality) {
			this.vitality = vitality;
			return this;
		}

		public SimpleFlightApparatus build() {
			return new SimpleFlightApparatus(usability, vitality);
		}
	}
}
