package me.paulf.wings.server.apparatus;

import me.paulf.wings.server.flight.Flight;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public interface FlightApparatus {
	void onFlight(final EntityPlayer player, final ItemStack stack, final Vec3d direction);

	void onLanding(final EntityPlayer player, final ItemStack stack, final Vec3d direction);

	boolean isUsable(final EntityPlayer player, final ItemStack stack);

	boolean isLandable(final EntityPlayer player, final ItemStack stack);

	FlightState createState(final Flight flight);

	interface FlightState {
		FlightState VOID = (player, stack) -> {};

		void onUpdate(final EntityPlayer player, final ItemStack stack);
	}
}
