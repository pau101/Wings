package com.pau101.wings.server.capability;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

final class FlightProvider implements ICapabilityProvider  {
	private final Flight flight;

	FlightProvider(Flight flight) {
		this.flight = flight;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == FlightCapability.CAPABILITY;
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == FlightCapability.CAPABILITY ? FlightCapability.CAPABILITY.cast(flight) : null;
	}
}
