package me.paulf.wings.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public final class HandlerSlot {
	private final IItemHandler handler;

	private final int slot;

	private HandlerSlot(final IItemHandler handler, final int slot) {
		this.handler = handler;
		this.slot = slot;
	}

	public ItemStack get() {
		return this.handler.getStackInSlot(this.slot);
	}

	public ItemStack insert(final ItemStack stack) {
		return this.handler.insertItem(this.slot, stack, false);
	}

	public ItemStack extract(final int amount) {
		return this.handler.extractItem(this.slot, amount, false);
	}

	public int getLimit() {
		return this.handler.getSlotLimit(this.slot);
	}

	public static HandlerSlot create(final IItemHandler handler, final int slot) {
		return new HandlerSlot(handler, slot);
	}
}
