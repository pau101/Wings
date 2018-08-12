package me.paulf.wings.util;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public interface ItemPlacing<T extends ICapabilityProvider> {
	IItemHandler getStorage(T provider);

	IntList getSlots();

	static <T extends ICapabilityProvider> ItemPlacing<T> forArmor(EntityEquipmentSlot slot) {
		return new ItemPlacing<T>() {
			@Override
			public IItemHandler getStorage(T provider) {
				return provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
			}

			@Override
			public IntList getSlots() {
				return IntLists.singleton(slot.getIndex());
			}

		};
	}
}
