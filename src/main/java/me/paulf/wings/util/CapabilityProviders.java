package me.paulf.wings.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class CapabilityProviders {
	private CapabilityProviders() {}

	public static <T> SimpleBuilder<T> builder(Capability<? super T> capability, T instance) {
		return new SimpleBuilder<>(capability, instance);
	}

	public static <T> SerializingBuilder<T, NBTBase> builder(Capability<T> capability) {
		return new SimpleBuilder<>(capability, capability.getDefaultInstance())
			.serializedBy(new NBTSerializer<T, NBTBase>() {
				@Override
				public NBTBase serialize(T instance) {
					return capability.writeNBT(instance, null);
				}

				@Override
				public T deserialize(NBTBase compound) {
					T instance = capability.getDefaultInstance();
					capability.readNBT(instance, null, compound);
					return instance;
				}
			});
	}

	private static abstract class Provider<T> implements ICapabilityProvider {
		final Capability<? super T> capability;

		T instance;

		private Provider(Capability<? super T> capability, T instance) {
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
	}

	private static final class SimpleProvider<T> extends Provider<T> {
		private SimpleProvider(Capability<? super T> capability, T instance) {
			super(capability, instance);
		}
	}

	private static final class SerializingProvider<T, N extends NBTBase> extends Provider<T> implements INBTSerializable<N> {
		final NBTSerializer<T, N> serializer;

		private SerializingProvider(Capability<? super T> capability, T instance, NBTSerializer<T, N> serializer) {
			super(capability, instance);
			this.serializer = serializer;
		}

		@Override
		public N serializeNBT() {
			return serializer.serialize(instance);
		}

		@Override
		public void deserializeNBT(N compound) {
			instance = serializer.deserialize(compound);
		}
	}

	public static abstract class Builder<T> {
		final Capability<? super T> capability;

		final T instance;

		private Builder(Capability<? super T> capability, T instance) {
			this.capability = capability;
			this.instance = instance;
		}

		public Builder<T> peek(Consumer<T> consumer) {
			consumer.accept(instance);
			return this;
		}
	}

	public static final class SimpleBuilder<T> extends Builder<T> {
		private SimpleBuilder(Capability<? super T> capability, T instance) {
			super(capability, instance);
		}

		public <N extends NBTBase> SerializingBuilder<T, N> serializedBy(NBTSerializer<T, N> serializer) {
			return new SerializingBuilder<>(capability, instance, serializer);
		}

		@Override
		public SimpleBuilder<T> peek(Consumer<T> consumer) {
			super.peek(consumer);
			return this;
		}

		public ICapabilityProvider build() {
			return new SimpleProvider<>(capability, instance);
		}
	}

	public static final class SerializingBuilder<T, N extends NBTBase> extends Builder<T> {
		private final NBTSerializer<T, N> serializer;

		private SerializingBuilder(Capability<? super T> capability, T instance, NBTSerializer<T, N> serializer) {
			super(capability, instance);
			this.serializer = serializer;
		}

		@Override
		public SerializingBuilder<T, N> peek(Consumer<T> consumer) {
			super.peek(consumer);
			return this;
		}

		public ICapabilityProvider build() {
			return new SerializingProvider<>(capability, instance, serializer);
		}
	}
}
