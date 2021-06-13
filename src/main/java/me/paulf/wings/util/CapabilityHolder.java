package me.paulf.wings.util;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CapabilityHolder<T extends ICapabilityProvider, R, S extends CapabilityHolder.State<T, R>> {
    private final Function<Capability<R>, S> presence;

    private S state;

    private CapabilityHolder(Function<Capability<R>, S> presence, S state) {
        this.presence = presence;
        this.state = state;
    }

    public S state() {
        return this.state;
    }

    public void inject(Capability<R> capability) {
        this.state = this.presence.apply(capability);
    }

    public interface State<T extends ICapabilityProvider, R> {
        default boolean has(T provider, @Nullable Direction side) {
            return this.get(provider, side).isPresent();
        }

        LazyOptional<R> get(T provider, @Nullable Direction side);

        <U extends R> CapabilityProviders.NonSerializingSingleBuilder<U> providerBuilder(U instance);
    }

    public static abstract class AbsentState<T extends ICapabilityProvider, R> implements State<T, R> {
        @Override
        public final boolean has(T provider, @Nullable Direction side) {
            return false;
        }

        @Override
        public final LazyOptional<R> get(T provider, @Nullable Direction side) {
            return LazyOptional.empty();
        }

        @Override
        public final <U extends R> CapabilityProviders.NonSerializingSingleBuilder<U> providerBuilder(U instance) {
            return CapabilityProviders.emptyBuilder();
        }
    }

    public static abstract class PresentState<T extends ICapabilityProvider, R> implements State<T, R> {
        protected final Capability<R> capability;

        protected PresentState(Capability<R> capability) {
            this.capability = capability;
        }

        @Override
        public final LazyOptional<R> get(T provider, @Nullable Direction side) {
            return provider.getCapability(this.capability, side);
        }

        @Override
        public final <U extends R> CapabilityProviders.NonSerializingSingleBuilder<U> providerBuilder(U instance) {
            return CapabilityProviders.builder(this.capability, instance);
        }
    }

    private static final class SimpleAbsentState<T extends ICapabilityProvider, R> extends AbsentState<T, R> {
    }

    private static final class SimplePresentState<T extends ICapabilityProvider, R> extends PresentState<T, R> {
        private SimplePresentState(Capability<R> capability) {
            super(capability);
        }
    }

    public static <T extends ICapabilityProvider, R> CapabilityHolder<T, R, State<T, R>> create() {
        return create(SimpleAbsentState::new, SimplePresentState::new);
    }

    public static <T extends ICapabilityProvider, R, S extends State<T, R>> CapabilityHolder<T, R, S> create(Supplier<S> absence, Function<Capability<R>, S> presence) {
        return new CapabilityHolder<>(presence, absence.get());
    }
}
