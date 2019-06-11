package me.paulf.wings.util;

import net.minecraft.nbt.NBTBase;

public interface NBTSerializer<T, N extends NBTBase> {
	N serialize(final T instance);

	T deserialize(final N compound);
}
