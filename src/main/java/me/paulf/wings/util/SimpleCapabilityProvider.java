package me.paulf.wings.util;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SimpleCapabilityProvider<T> implements ICapabilityProvider {
	private final Capability<T> capability;

	private final T instance;

	private SimpleCapabilityProvider(Capability<T> capability, T instance) {
		this.capability = capability;
		this.instance = instance;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return this.capability == capability;
	}

	@Nullable
	@Override
	public <C> C getCapability(@Nonnull Capability<C> capability, @Nullable EnumFacing facing) {
		return this.capability == capability ? this.capability.cast(instance) : null;
	}

	public static <T> SimpleCapabilityProvider<T> create(Capability<T> capability, T instance) {
		return new SimpleCapabilityProvider<>(capability, instance);
	}
}
