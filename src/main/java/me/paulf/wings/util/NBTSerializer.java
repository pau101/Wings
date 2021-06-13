package me.paulf.wings.util;

import net.minecraft.nbt.INBT;

public interface NBTSerializer<T, N extends INBT> {
    N serialize(T instance);

    T deserialize(N compound);
}
