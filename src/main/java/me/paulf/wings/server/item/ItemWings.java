package me.paulf.wings.server.item;

import com.google.common.collect.ImmutableSet;
import me.paulf.wings.WingsMod;
import me.paulf.wings.server.apparatus.FlightApparatus;
import me.paulf.wings.server.apparatus.FlightApparatuses;
import me.paulf.wings.server.apparatus.SimpleFlightApparatus;
import me.paulf.wings.server.sound.WingsSounds;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.HandlerSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class ItemWings extends Item {
	private final ImmutableSet<EnumEnchantmentType> allowedEnchantmentTypes;

	private final Consumer<CapabilityProviders.CompositeBuilder> capabilities;

	private WingSettings settings;

	private static final float EXHAUSTION_ALTERNATIVE_FACTOR = 0.2F;

	private ItemWings(ImmutableSet<EnumEnchantmentType> allowedEnchantmentTypes, Consumer<CapabilityProviders.CompositeBuilder> capabilities, WingSettings settings) {
		this.allowedEnchantmentTypes = allowedEnchantmentTypes;
		this.capabilities = capabilities;
		this.settings = settings;
	}

	public void setSettings(WingSettings settings) {
		this.settings = settings;
		setMaxDamage(settings.getItemDurability());
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

	public static ItemWings create(Consumer<CapabilityProviders.CompositeBuilder> capabilities, WingSettings attributes) {
		ItemWings wings = new ItemWings(
			ImmutableSet.of(
				EnumEnchantmentType.ALL,
				EnumEnchantmentType.BREAKABLE,
				EnumEnchantmentType.WEARABLE
			),
			capabilities,
			attributes
		);
		wings.setMaxStackSize(1);
		wings.setMaxDamage(attributes.getItemDurability());
		return wings;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound tag) {
		CapabilityProviders.CompositeBuilder builder = CapabilityProviders.builder()
			.add(FlightApparatuses.providerBuilder(SimpleFlightApparatus.builder()
				.withFlight(((player, wings, direction) -> {
					int distance = Math.round((float) direction.length() * 100.0F);
					if (distance > 0) {
						if (settings.getAlternativeMode()) {
							player.addExhaustion(EXHAUSTION_ALTERNATIVE_FACTOR * distance * settings.getFlyingExertion());
						}
						else {
							player.addExhaustion(distance * settings.getFlyingExertion());
						}
					}
				}))
				.withLanding(((player, wings, direction) -> {
					if (settings.getAlternativeMode()) {
						player.addExhaustion(EXHAUSTION_ALTERNATIVE_FACTOR * settings.getLandingExertion());
					}
					else {
						player.addExhaustion(settings.getLandingExertion());
					}
				}))
				.withUsability((player, wings) -> (!wings.isItemStackDamageable() || wings.getItemDamage() < wings.getMaxDamage() - 1) && player.getFoodStats().getFoodLevel() >= settings.getRequiredFlightSatiation())
				.withLandability((player, wings) -> player.getFoodStats().getFoodLevel() >= settings.getRequiredLandSatiation())
				.withVitality(flight -> new FlightApparatus.FlightState() {
					//in alternative mode DAMAGE_RATE will be 1 instead of 20
					private static final int DAMAGE_RATE = 20;
					private static final int DAMAGE_RATE_ALTERNATIVE = 1;
					//set repair value of minerals
					private static final int REPAIR_FAIRY_DUST = 160;
					private static final int REPAIR_AMETHYST = 1080;

					private int flightTime;

					//function for repair
					public void repairStack(EntityPlayer player, ItemStack stack, Item item, int repair) {
						if (stack.getItemDamage() >= repair) {
							if (player.inventory.hasItemStack(new ItemStack(item))) {
								int slot = player.inventory.getSlotFor(new ItemStack(item));
								ItemStack itemStack = player.inventory.getStackInSlot(slot);
								ItemStack updateItemStack = player.inventory.decrStackSize(slot, itemStack.getCount() - 1);
								player.inventory.setInventorySlotContents(slot, updateItemStack);
								stack.damageItem(-repair, player);
							}
						}
					}

					@Override
					public void onUpdate(EntityPlayer player, ItemStack stack) {
						if (flight.isFlying()) {
							if (settings.getAlternativeMode()) {
								if (flightTime++ % DAMAGE_RATE_ALTERNATIVE == (DAMAGE_RATE_ALTERNATIVE - 1)) {
									stack.damageItem(1, player);
								}
							}
							else {
								if (flightTime++ % DAMAGE_RATE == (DAMAGE_RATE - 1)) {
									stack.damageItem(1, player);
								}
							}
						} else {
							flightTime = 0;
						}
						//consume minerals to repair
						if (settings.getAlternativeMode()) {
							repairStack(player, stack, WingsItems.FAIRY_DUST, REPAIR_FAIRY_DUST);
							repairStack(player, stack, WingsItems.AMETHYST, REPAIR_AMETHYST);
						}
					}
				})
				.build()
			)
			.build());
		capabilities.accept(builder);
		return builder.build();
	}
}
