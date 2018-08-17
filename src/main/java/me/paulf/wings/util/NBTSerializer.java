package me.paulf.wings.util;

import net.minecraft.nbt.NBTBase;

public interface NBTSerializer<T, N extends NBTBase> {
	N serialize(T instance);

	T deserialize(N compound);
}
