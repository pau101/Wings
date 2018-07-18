package com.pau101.wings.server.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import com.pau101.wings.WingsMod;
import com.pau101.wings.server.capability.Flight;
import com.pau101.wings.server.capability.FlightCapability;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;

public final class ItemWings extends Item implements IBauble, IRenderBauble {
	public ItemWings() {
		setMaxStackSize(1);
		setHasSubtypes(true);
	}

	public ItemStack createStack(StandardWing type) {
		return new ItemStack(this, 1, type.getMeta());
	}

	@Override
	public BaubleType getBaubleType(ItemStack stack) {
		return BaubleType.BODY;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase entity) {
		FlightCapability.ifPlayer(entity, (player, flight) -> flight.update(player));
	}

	@Override
	public void onEquipped(ItemStack stack, EntityLivingBase entity) {
		resetIsFlying(entity);
	}

	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase entity) {
		resetIsFlying(entity);
	}

	private void resetIsFlying(EntityLivingBase entity) {
		FlightCapability.ifPlayer(entity, e -> !e.world.isRemote, (player, flight) ->
			flight.setIsFlying(false, Flight.PlayerSet.ALL)
		);
	}

	@Override
	public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float delta) {
		WingsMod.instance().renderWings(stack, player, type, delta);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		//noinspection deprecation
		return I18n.translateToLocalFormatted(super.getItemStackDisplayName(stack), I18n.translateToLocal(StandardWing.fromMeta(stack).getUnlocalizedName()));
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab)) {
			StandardWing.stream().forEach(t -> items.add(createStack(t)));
		}
	}
}
