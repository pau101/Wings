package me.paulf.wings.server.apparatus;

import me.paulf.wings.server.flight.Flight;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;

public interface FlightApparatus {
	void onFlight(final PlayerEntity player, final ItemStack stack, final Vector3d direction);

	void onLanding(final PlayerEntity player, final ItemStack stack, final Vector3d direction);

	boolean isUsable(final PlayerEntity player, final ItemStack stack);

	boolean isLandable(final PlayerEntity player, final ItemStack stack);

	FlightState createState(final Flight flight);

	interface FlightState {
		FlightState VOID = (player, stack) -> {};

		void onUpdate(final PlayerEntity player, final ItemStack stack);
	}
}
