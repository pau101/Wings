package me.paulf.wings.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;

public interface ItemPlacing<T extends ICapabilityProvider> {
	void enumerate(final T provider, final ImmutableList.Builder<HandlerSlot> handlers);

	static <T extends EntityLivingBase> ItemPlacing<T> forArmor(final EntityEquipmentSlot slot) {
		return (provider, handlers) -> handlers.add(
			HandlerSlot.create(provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH), slot.getIndex())
		);
	}
}
