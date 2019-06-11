package me.paulf.wings.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.util.function.Consumer;
import java.util.function.Function;

public final class SimpleStorage<T> implements Capability.IStorage<T> {
	private final Function<T, NBTTagCompound> serializer;

	private final Consumer<NBTTagCompound> deserializer;

	private SimpleStorage(final Function<T, NBTTagCompound> serializer, final Consumer<NBTTagCompound> deserializer) {
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	@Override
	public NBTBase writeNBT(final Capability<T> capability, final T instance, final EnumFacing side) {
		return this.serializer.apply(instance);
	}

	@Override
	public void readNBT(final Capability<T> capability, final T instance, final EnumFacing side, final NBTBase tag) {
		this.deserializer.accept(tag instanceof NBTTagCompound ? (NBTTagCompound) tag : new NBTTagCompound());
	}

	public static <T> SimpleStorage<T> ofVoid() {
		return new SimpleStorage<>(instance -> null, tag -> {});
	}

	public static <T> SimpleStorage<T> of(final Function<T, NBTTagCompound> serializer, final Consumer<NBTTagCompound> deserializer) {
		return new SimpleStorage<>(serializer, deserializer);
	}
}
