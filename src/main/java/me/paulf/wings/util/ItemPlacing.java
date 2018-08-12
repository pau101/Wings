package me.paulf.wings.util;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;

public interface ItemPlacing<T extends ICapabilityProvider> {
	IItemHandler getStorage(T provider);

	IntList getSlots();
}
