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

	private SimpleStorage(Function<T, NBTTagCompound> serializer, Consumer<NBTTagCompound> deserializer) {
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	@Override
	public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
		return serializer.apply(instance);
	}

	@Override
	public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase tag) {
		deserializer.accept(tag instanceof NBTTagCompound ? (NBTTagCompound) tag : new NBTTagCompound());
	}

	public static <T> SimpleStorage<T> ofVoid() {
		return new SimpleStorage<>(instance -> null, tag -> {});
	}

	public static <T> SimpleStorage<T> of(Function<T, NBTTagCompound> serializer, Consumer<NBTTagCompound> deserializer) {
		return new SimpleStorage<>(serializer, deserializer);
	}
}
