package me.paulf.wings.util;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;

public final class ItemAccessor<T extends ICapabilityProvider> {
	private final ImmutableList<ItemPlacing<T>> placings;

	private ItemAccessor(ImmutableList<ItemPlacing<T>> placings) {
		this.placings = placings;
	}

	public Iterable<HandlerSlot> enumerate(T provider) {
		ImmutableList.Builder<HandlerSlot> slots = ImmutableList.builder();
		for (ItemPlacing<T> placing : placings) {
			IItemHandler handler = placing.getStorage(provider);
			for (IntIterator it = placing.getSlots().iterator(); it.hasNext(); ) {
				slots.add(HandlerSlot.create(handler, it.nextInt()));
			}
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

		private Builder(ImmutableList.Builder<ItemPlacing<T>> placings) {
			this.placings = placings;
		}

		public Builder addPlacing(ItemPlacing<T> placing) {
			placings.add(placing);
			return this;
		}

		public ItemAccessor<T> build() {
			return new ItemAccessor<>(placings.build());
		}
	}
}
