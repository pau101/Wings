package me.paulf.wings.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;

public interface ItemPlacing<T extends ICapabilityProvider> {
	void enumerate(final T provider, final ImmutableList.Builder<HandlerSlot> handlers);

	static <T extends LivingEntity> ItemPlacing<T> forArmor(final EquipmentSlotType slot) {
		return (provider, handlers) -> provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.EAST)
			.ifPresent(handler -> handlers.add(HandlerSlot.create(handler, slot.getIndex())));
	}
}
