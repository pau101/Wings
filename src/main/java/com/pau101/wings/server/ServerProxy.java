package com.pau101.wings.server;

import com.pau101.wings.Proxy;
import com.pau101.wings.server.capability.Flight;
import net.minecraft.entity.player.EntityPlayer;

public final class ServerProxy extends Proxy {
	@Override
	protected void addFlightListeners(EntityPlayer player, Flight flight) {}
}
