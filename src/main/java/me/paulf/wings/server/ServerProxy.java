package me.paulf.wings.server;

import me.paulf.wings.Proxy;
import me.paulf.wings.server.flight.Flight;
import net.minecraft.entity.player.EntityPlayer;

public final class ServerProxy extends Proxy {
	@Override
	protected void addSpecializedFlightListeners(EntityPlayer player, Flight flight) {}
}
