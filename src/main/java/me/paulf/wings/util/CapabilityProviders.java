package me.paulf.wings.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
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

	public static ICapabilityProvider empty() {
		return EmptyProvider.INSTANCE;
	}

	public static <T> NonSerializingSingleBuilder<T> emptyBuilder() {
		//noinspection unchecked
		return (EmptySingleBuilder<T>) EmptySingleBuilder.INSTANCE;
	}

	public static <T> NonSerializingSingleBuilder<T> builder(Capability<? super T> capability, T instance) {
		return new NonSerializingSingleBuilderImpl<>(capability, instance);
	}

	public static <T> SingleBuilder<T> builder(Capability<T> capability) {
		return new NonSerializingSingleBuilderImpl<>(capability, capability.getDefaultInstance())
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

	public static CompositeBuilder builder() {
		return new CompositeBuilderImpl();
	}

	public interface CompositeBuilder {
		CompositeBuilder add(ICapabilityProvider provider);

		ICapabilityProvider build();
	}

	private static final class CompositeBuilderImpl implements CompositeBuilder {
		private final ImmutableList.Builder<ICapabilityProvider> providers;

		private CompositeBuilderImpl() {
			this(ImmutableList.builder());
		}

		private CompositeBuilderImpl(ImmutableList.Builder<ICapabilityProvider> providers) {
			this.providers = providers;
		}

		@Override
		public CompositeBuilder add(ICapabilityProvider provider) {
			providers.add(provider);
			return this;
		}

		@Override
		public ICapabilityProvider build() {
			ImmutableList<ICapabilityProvider> providers = this.providers.build();
			switch (providers.size()) {
				case 0:
					return empty();
				case 1:
					return Iterables.getOnlyElement(providers);
				default:
					return new CompositeProvider(providers);
			}
		}
	}

	private static final class CompositeProvider implements ICapabilityProvider {
		private final ImmutableList<ICapabilityProvider> providers;

		private CompositeProvider(ImmutableList<ICapabilityProvider> providers) {
			this.providers = providers;
		}

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
			for (ICapabilityProvider provider : providers) {
				if (provider.hasCapability(capability, facing)) {
					return true;
				}
			}
			return false;
		}

		@Nullable
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
			for (ICapabilityProvider provider : providers) {
				T instance = provider.getCapability(capability, facing);
				if (instance != null) {
					return instance;
				}
			}
			return null;
		}
	}

	private static final class EmptySingleBuilder<T> implements NonSerializingSingleBuilder<T> {
		private static final EmptySingleBuilder<?> INSTANCE = new EmptySingleBuilder<>();

		@Override
		public <N extends NBTBase> SingleBuilder<T> serializedBy(NBTSerializer<T, N> serializer) {
			return this;
		}

		@Override
		public SingleBuilder<T> peek(Consumer<T> consumer) {
			return this;
		}

		@Override
		public ICapabilityProvider build() {
			return empty();
		}
	}

	private static final class EmptyProvider implements ICapabilityProvider {
		private static final EmptyProvider INSTANCE = new EmptyProvider();

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
			return false;
		}

		@Nullable
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
			return null;
		}
	}

	private static abstract class SingleProvider<T> implements ICapabilityProvider {
		final Capability<? super T> capability;

		T instance;

		private SingleProvider(Capability<? super T> capability, T instance) {
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

	private static final class SimpleSingleProvider<T> extends SingleProvider<T> {
		private SimpleSingleProvider(Capability<? super T> capability, T instance) {
			super(capability, instance);
		}
	}

	private static final class SerializingSingleProvider<T, N extends NBTBase> extends SingleProvider<T> implements INBTSerializable<N> {
		final NBTSerializer<T, N> serializer;

		private SerializingSingleProvider(Capability<? super T> capability, T instance, NBTSerializer<T, N> serializer) {
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

	private static abstract class AbstractSingleBuilder<T> implements SingleBuilder<T> {
		final Capability<? super T> capability;

		final T instance;

		AbstractSingleBuilder(Capability<? super T> capability, T instance) {
			this.capability = capability;
			this.instance = instance;
		}
	}

	public interface SingleBuilder<T> {
		SingleBuilder<T> peek(Consumer<T> consumer);

		ICapabilityProvider build();
	}

	public interface NonSerializingSingleBuilder<T> extends SingleBuilder<T> {
		<N extends NBTBase> SingleBuilder<T> serializedBy(NBTSerializer<T, N> serializer);
	}

	private static final class NonSerializingSingleBuilderImpl<T> extends AbstractSingleBuilder<T> implements NonSerializingSingleBuilder<T> {
		private NonSerializingSingleBuilderImpl(Capability<? super T> capability, T instance) {
			super(capability, instance);
		}

		@Override
		public <N extends NBTBase> SerializingSingleBuilderImpl<T, N> serializedBy(NBTSerializer<T, N> serializer) {
			return new SerializingSingleBuilderImpl<>(capability, instance, serializer);
		}

		@Override
		public NonSerializingSingleBuilder<T> peek(Consumer<T> consumer) {
			consumer.accept(instance);
			return this;
		}

		@Override
		public ICapabilityProvider build() {
			return new SimpleSingleProvider<>(capability, instance);
		}
	}

	private static final class SerializingSingleBuilderImpl<T, N extends NBTBase> extends AbstractSingleBuilder<T> implements SingleBuilder<T> {
		private final NBTSerializer<T, N> serializer;

		private SerializingSingleBuilderImpl(Capability<? super T> capability, T instance, NBTSerializer<T, N> serializer) {
			super(capability, instance);
			this.serializer = serializer;
		}

		@Override
		public SerializingSingleBuilderImpl<T, N> peek(Consumer<T> consumer) {
			consumer.accept(instance);
			return this;
		}

		@Override
		public ICapabilityProvider build() {
			return new SerializingSingleProvider<>(capability, instance, serializer);
		}
	}
}
