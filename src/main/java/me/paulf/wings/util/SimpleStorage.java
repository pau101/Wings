package me.paulf.wings.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import java.util.function.Consumer;
import java.util.function.Function;

public final class SimpleStorage<T> implements Capability.IStorage<T> {
    private final Function<T, CompoundNBT> serializer;

    private final Consumer<CompoundNBT> deserializer;

    private SimpleStorage(Function<T, CompoundNBT> serializer, Consumer<CompoundNBT> deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
        return this.serializer.apply(instance);
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, Direction side, INBT tag) {
        this.deserializer.accept(tag instanceof CompoundNBT ? (CompoundNBT) tag : new CompoundNBT());
    }

    public static <T> SimpleStorage<T> ofVoid() {
        return new SimpleStorage<>(instance -> null, tag -> {
        });
    }

    public static <T> SimpleStorage<T> of(Function<T, CompoundNBT> serializer, Consumer<CompoundNBT> deserializer) {
        return new SimpleStorage<>(serializer, deserializer);
    }
}
