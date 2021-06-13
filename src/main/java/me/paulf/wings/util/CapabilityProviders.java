package me.paulf.wings.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class CapabilityProviders {
    private CapabilityProviders() {
    }

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
            .serializedBy(new NBTSerializer<T, INBT>() {
                @Override
                public INBT serialize(T instance) {
                    return capability.writeNBT(instance, null);
                }

                @Override
                public T deserialize(INBT compound) {
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
            this.providers.add(provider);
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
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            for (ICapabilityProvider provider : this.providers) {
                LazyOptional<T> instance = provider.getCapability(capability, facing);
                if (instance.isPresent()) {
                    return instance;
                }
            }
            return LazyOptional.empty();
        }
    }

    private static final class EmptySingleBuilder<T> implements NonSerializingSingleBuilder<T> {
        private static final EmptySingleBuilder<?> INSTANCE = new EmptySingleBuilder<>();

        @Override
        public <N extends INBT> SingleBuilder<T> serializedBy(NBTSerializer<T, N> serializer) {
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
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
            return LazyOptional.empty();
        }
    }

    private static abstract class SingleProvider<T> implements ICapabilityProvider {
        final Capability<? super T> capability;

        T instance;
        LazyOptional<T> lazy;

        private SingleProvider(Capability<? super T> capability, T instance) {
            this.capability = capability;
            this.instance = instance;
            this.lazy = LazyOptional.of(() -> instance);
        }

        @Override
        public <C> LazyOptional<C> getCapability(@Nonnull Capability<C> capability, @Nullable Direction facing) {
            return this.capability == capability ? this.lazy.cast() : LazyOptional.empty();
        }
    }

    private static final class SimpleSingleProvider<T> extends SingleProvider<T> {
        private SimpleSingleProvider(Capability<T> capability, T instance) {
            super(capability, instance);
        }
    }

    private static final class SerializingSingleProvider<T, N extends INBT> extends SingleProvider<T> implements INBTSerializable<N> {
        final NBTSerializer<T, N> serializer;

        private SerializingSingleProvider(Capability<? super T> capability, T instance, NBTSerializer<T, N> serializer) {
            super(capability, instance);
            this.serializer = serializer;
        }

        @Override
        public N serializeNBT() {
            return this.serializer.serialize(this.instance);
        }

        @Override
        public void deserializeNBT(N compound) {
            this.instance = this.serializer.deserialize(compound);
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
        <N extends INBT> SingleBuilder<T> serializedBy(NBTSerializer<T, N> serializer);
    }

    private static final class NonSerializingSingleBuilderImpl<T> extends AbstractSingleBuilder<T> implements NonSerializingSingleBuilder<T> {
        private NonSerializingSingleBuilderImpl(Capability<? super T> capability, T instance) {
            super(capability, instance);
        }

        @Override
        public <N extends INBT> SerializingSingleBuilderImpl<T, N> serializedBy(NBTSerializer<T, N> serializer) {
            return new SerializingSingleBuilderImpl<>(this.capability, this.instance, serializer);
        }

        @Override
        public NonSerializingSingleBuilder<T> peek(Consumer<T> consumer) {
            consumer.accept(this.instance);
            return this;
        }

        @Override
        public ICapabilityProvider build() {
            return new SimpleSingleProvider<>(this.capability, this.instance);
        }
    }

    private static final class SerializingSingleBuilderImpl<T, N extends INBT> extends AbstractSingleBuilder<T> implements SingleBuilder<T> {
        private final NBTSerializer<T, N> serializer;

        private SerializingSingleBuilderImpl(Capability<? super T> capability, T instance, NBTSerializer<T, N> serializer) {
            super(capability, instance);
            this.serializer = serializer;
        }

        @Override
        public SerializingSingleBuilderImpl<T, N> peek(Consumer<T> consumer) {
            consumer.accept(this.instance);
            return this;
        }

        @Override
        public ICapabilityProvider build() {
            return new SerializingSingleProvider<>(this.capability, this.instance, this.serializer);
        }
    }
}
