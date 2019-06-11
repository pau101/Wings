package me.paulf.wings.util;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CapabilityHolder<T extends ICapabilityProvider, R, S extends CapabilityHolder.State<T, R>> {
	private final Function<Capability<R>, S> presence;

	private S state;

	private CapabilityHolder(final Function<Capability<R>, S> presence, final S state) {
		this.presence = presence;
		this.state = state;
	}

	public S state() {
		return this.state;
	}

	public void inject(final Capability<R> capability) {
		this.state = this.presence.apply(capability);
	}

	public interface State<T extends ICapabilityProvider, R> {
		boolean has(T provider, @Nullable EnumFacing side);

		@Nullable
		R get(T provider, @Nullable EnumFacing side);

		<U extends R> CapabilityProviders.NonSerializingSingleBuilder<U> providerBuilder(U instance);
	}

	public static abstract class AbsentState<T extends ICapabilityProvider, R> implements State<T, R> {
		@Override
		public final boolean has(final T provider, @Nullable final EnumFacing side) {
			return false;
		}

		@Nullable
		@Override
		public final R get(final T provider, @Nullable final EnumFacing side) {
			return null;
		}

		@Override
		public final <U extends R> CapabilityProviders.NonSerializingSingleBuilder<U> providerBuilder(final U instance) {
			return CapabilityProviders.emptyBuilder();
		}
	}

	public static abstract class PresentState<T extends ICapabilityProvider, R> implements State<T, R> {
		protected final Capability<R> capability;

		protected PresentState(final Capability<R> capability) {
			this.capability = capability;
		}

		@Override
		public final boolean has(final T provider, @Nullable final EnumFacing side) {
			return provider.hasCapability(this.capability, side);
		}

		@Nullable
		@Override
		public final R get(final T provider, @Nullable final EnumFacing side) {
			return provider.getCapability(this.capability, side);
		}

		@Override
		public final <U extends R> CapabilityProviders.NonSerializingSingleBuilder<U> providerBuilder(final U instance) {
			return CapabilityProviders.builder(this.capability, instance);
		}
	}

	private static final class SimpleAbsentState<T extends ICapabilityProvider, R> extends AbsentState<T, R> {}

	private static final class SimplePresentState<T extends ICapabilityProvider, R> extends PresentState<T, R> {
		private SimplePresentState(final Capability<R> capability) {
			super(capability);
		}
	}

	public static <T extends ICapabilityProvider, R> CapabilityHolder<T, R, State<T, R>> create() {
		return create(SimpleAbsentState::new, SimplePresentState::new);
	}

	public static <T extends ICapabilityProvider, R, S extends State<T, R>> CapabilityHolder<T, R, S> create(final Supplier<S> absence, final Function<Capability<R>, S> presence) {
		return new CapabilityHolder<>(presence, absence.get());
	}
}
