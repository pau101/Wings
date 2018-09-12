package me.paulf.wings.server.apparatus;

import me.paulf.wings.server.flight.Flight;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public interface FlightApparatus {
	void onFlight(EntityPlayer player, ItemStack stack, Vec3d direction);

	void onLanding(EntityPlayer player, ItemStack stack, Vec3d direction);

	boolean isUsable(EntityPlayer player, ItemStack stack);

	boolean isLandable(EntityPlayer player, ItemStack stack);

	FlightState createState(Flight flight);

	interface FlightState {
		FlightState VOID = (player, stack) -> {};

		void onUpdate(EntityPlayer player, ItemStack stack);
	}
}
