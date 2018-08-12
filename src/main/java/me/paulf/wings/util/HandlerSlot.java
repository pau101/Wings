package me.paulf.wings.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public final class HandlerSlot {
	private final IItemHandler handler;

	private final int slot;

	private HandlerSlot(IItemHandler handler, int slot) {
		this.handler = handler;
		this.slot = slot;
	}

	public ItemStack get() {
		return handler.getStackInSlot(slot);
	}

	public ItemStack insert(ItemStack stack) {
		return handler.insertItem(slot, stack, false);
	}

	public ItemStack extract(int amount) {
		return handler.extractItem(slot, amount, false);
	}

	public int getLimit() {
		return handler.getSlotLimit(slot);
	}

	public static HandlerSlot create(IItemHandler handler, int slot) {
		return new HandlerSlot(handler, slot);
	}
}
