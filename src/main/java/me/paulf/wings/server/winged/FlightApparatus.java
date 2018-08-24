package me.paulf.wings.server.winged;

import me.paulf.wings.server.flight.Flight;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface FlightApparatus {
	boolean isUsable(ItemStack stack);

	FlightState createState(Flight flight);

	interface FlightState {
		FlightState VOID = (player, stack) -> {};

		void onUpdate(EntityPlayer player, ItemStack stack);
	}
}
