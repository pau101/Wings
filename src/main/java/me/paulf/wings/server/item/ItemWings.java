package me.paulf.wings.server.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.render.IRenderBauble;
import me.paulf.wings.WingsMod;
import me.paulf.wings.server.capability.Flight;
import me.paulf.wings.server.capability.FlightCapability;
import me.paulf.wings.server.flight.WingType;
import me.paulf.wings.server.sound.WingsSounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public final class ItemWings extends Item implements IBauble, IRenderBauble {
	private static final BaubleType BAUBLE_TYPE = BaubleType.BODY;

	private final StandardWing type;

	private ItemWings(StandardWing type) {
		this.type = type;
	}

	public StandardWing getType() {
		return type;
	}

	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack ingredient) {
		return WingsDict.test(ingredient, WingsDict.FAIRY_DUST);
	}

	@Override
	public BaubleType getBaubleType(ItemStack stack) {
		return BAUBLE_TYPE;
	}

	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase entity) {
		FlightCapability.ifPlayer(entity, (player, flight) -> flight.onWornUpdate(player));
	}

	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
		player.playSound(WingsSounds.ITEM_ARMOR_EQUIP_WINGS, 1.0F, 1.0F);
	}

	@Override
	public void onUnequipped(ItemStack stack, EntityLivingBase entity) {
		FlightCapability.ifPlayer(entity, e -> !e.world.isRemote, (player, flight) ->
			flight.setIsFlying(false, Flight.PlayerSet.ofAll())
		);
	}

	@Override
	public void onPlayerBaubleRender(ItemStack stack, EntityPlayer player, RenderType type, float delta) {
		WingsMod.instance().renderWings(getType(), player, type, delta);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
		for (int slot : getBaubleType(stack).getValidSlots()) {
			if (handler.getStackInSlot(slot).isEmpty() && handler.isItemValidForSlot(slot, stack, player)) {
				ItemStack copy = stack.copy();
				handler.setStackInSlot(slot, copy);
				onEquipped(copy, player);
				stack.setCount(0);
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			}
		}
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	public static boolean isUsable(ItemStack stack) {
		return !stack.isEmpty() && stack.getItemDamage() < stack.getMaxDamage() - 1;
	}

	public static ItemStack get(EntityPlayer player) {
		IBaublesItemHandler inv = BaublesApi.getBaublesHandler(player);
		for (int slot : BAUBLE_TYPE.getValidSlots()) {
			ItemStack stack = inv.getStackInSlot(slot);
			if (stack.getItem() instanceof ItemWings && isUsable(stack)) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	public static WingType getType(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemWings) {
			return ((ItemWings) item).getType();
		}
		return WingType.ABSENT;
	}

	public static ItemWings create(StandardWing type) {
		ItemWings item = new ItemWings(type);
		item.setMaxStackSize(1);
		item.setMaxDamage(540);
		return item;
	}
}
