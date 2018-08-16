package me.paulf.wings.server.item;

import com.google.common.collect.ImmutableSet;
import me.paulf.wings.WingsMod;
import me.paulf.wings.server.flight.WingType;
import me.paulf.wings.server.sound.WingsSounds;
import me.paulf.wings.util.HandlerSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public final class ItemWings extends Item {
	private final ImmutableSet<EnumEnchantmentType> allowedEnchantmentTypes;

	private final StandardWing type;

	private ItemWings(StandardWing type, ImmutableSet<EnumEnchantmentType> allowedEnchantmentTypes) {
		this.type = type;
		this.allowedEnchantmentTypes = allowedEnchantmentTypes;
	}

	public StandardWing getType() {
		return type;
	}

	@Override
	public EntityEquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EntityEquipmentSlot.CHEST;
	}

	@Override
	public int getItemEnchantability() {
		return 1;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return allowedEnchantmentTypes.contains(enchantment.type);
	}

	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack ingredient) {
		return WingsDict.test(ingredient, WingsDict.FAIRY_DUST);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		for (HandlerSlot slot : WingsMod.instance().getWingsAccessor().enumerate(player)) {
			ItemStack split = stack.splitStack(1);
			ItemStack remaining = slot.insert(split);
			stack.grow(remaining.getCount());
			if (remaining.getCount() < split.getCount()) {
				player.playSound(WingsSounds.ITEM_ARMOR_EQUIP_WINGS, 1.0F, 1.0F);
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			}
		}
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	public static boolean isUsable(ItemStack stack) {
		return !stack.isEmpty() && stack.getItemDamage() < stack.getMaxDamage() - 1;
	}

	public static ItemStack get(EntityPlayer player) {
		for (HandlerSlot slot : WingsMod.instance().getWingsAccessor().enumerate(player)) {
			ItemStack stack = slot.get();
			if (stack.getItem() instanceof ItemWings) {
				return stack;
			}
			if (!stack.isEmpty() && !(stack.getItem() instanceof ItemArmor)) {
				break;
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

	public static boolean test(ItemStack stack) {
		return stack.getItem() instanceof ItemWings;
	}

	public static ItemWings create(StandardWing type) {
		ItemWings wings = new ItemWings(type, ImmutableSet.of(
			EnumEnchantmentType.ALL,
			EnumEnchantmentType.BREAKABLE,
			EnumEnchantmentType.WEARABLE
		));
		wings.setMaxStackSize(1);
		wings.setMaxDamage(480);
		return wings;
	}
}
