package me.paulf.wings.util;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public final class ItemAccessor<T extends ICapabilityProvider> {
	private final ImmutableList<ItemPlacing<T>> placings;

	private ItemAccessor(final ImmutableList<ItemPlacing<T>> placings) {
		this.placings = placings;
	}

	public Iterable<HandlerSlot> enumerate(final T provider) {
		final ImmutableList.Builder<HandlerSlot> slots = ImmutableList.builder();
		for (final ItemPlacing<T> placing : this.placings) {
			placing.enumerate(provider, slots);
		}
		return slots.build();
	}

	public static <T extends ICapabilityProvider> ItemAccessor<T> none() {
		return new ItemAccessor<>(ImmutableList.of());
	}

	public static <T extends ICapabilityProvider> Builder<T> builder() {
		return new Builder<>();
	}

	public static final class Builder<T extends ICapabilityProvider> {
		private final ImmutableList.Builder<ItemPlacing<T>> placings;

		private Builder() {
			this(ImmutableList.builder());
		}

		private Builder(final ImmutableList.Builder<ItemPlacing<T>> placings) {
			this.placings = placings;
		}

		public Builder addPlacing(final ItemPlacing<T> placing) {
			this.placings.add(placing);
			return this;
		}

		public ItemAccessor<T> build() {
			return new ItemAccessor<>(this.placings.build());
		}
	}
}
