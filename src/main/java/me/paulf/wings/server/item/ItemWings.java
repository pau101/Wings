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

	private ItemWings(final ImmutableSet<EnumEnchantmentType> allowedEnchantmentTypes, final Consumer<CapabilityProviders.CompositeBuilder> capabilities, final WingSettings settings) {
		this.allowedEnchantmentTypes = allowedEnchantmentTypes;
		this.capabilities = capabilities;
		this.settings = settings;
	}

	public void setSettings(final WingSettings settings) {
		this.settings = settings;
		this.setMaxDamage(settings.getItemDurability());
	}

	@Override
	public EntityEquipmentSlot getEquipmentSlot(final ItemStack stack) {
		return EntityEquipmentSlot.CHEST;
	}

	@Override
	public int getItemEnchantability() {
		return 1;
	}

	@Override
	public boolean canApplyAtEnchantingTable(final ItemStack stack, final Enchantment enchantment) {
		return this.allowedEnchantmentTypes.contains(enchantment.type);
	}

	@Override
	public boolean getIsRepairable(final ItemStack stack, final ItemStack ingredient) {
		return WingsDict.test(ingredient, WingsDict.FAIRY_DUST);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		for (final HandlerSlot slot : WingsMod.instance().getWingsAccessor().enumerate(player)) {
			final ItemStack split = stack.splitStack(1);
			final ItemStack remaining = slot.insert(split);
			stack.grow(remaining.getCount());
			if (remaining.getCount() < split.getCount()) {
				player.playSound(WingsSounds.ITEM_ARMOR_EQUIP_WINGS, 1.0F, 1.0F);
				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			}
		}
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	public static ItemWings create(final Consumer<CapabilityProviders.CompositeBuilder> capabilities, final WingSettings attributes) {
		final ItemWings wings = new ItemWings(
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
	public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final NBTTagCompound tag) {
		final CapabilityProviders.CompositeBuilder builder = CapabilityProviders.builder()
			.add(FlightApparatuses.providerBuilder(SimpleFlightApparatus.builder()
				.withFlight(((player, wings, direction) -> {
					int distance = Math.round((float) direction.length() * 100.0F);
					if (distance > 0) {
						player.addExhaustion(distance * this.settings.getFlyingExertion());
					}
				}))
				.withLanding(((player, wings, direction) -> player.addExhaustion(this.settings.getLandingExertion())))
				.withUsability((player, wings) -> (!wings.isItemStackDamageable() || wings.getItemDamage() < wings.getMaxDamage() - 1) && player.getFoodStats().getFoodLevel() >= this.settings.getRequiredFlightSatiation())
				.withLandability((player, wings) -> player.getFoodStats().getFoodLevel() >= this.settings.getRequiredLandSatiation())
				.withVitality(flight -> new FlightApparatus.FlightState() {
					private static final int DAMAGE_RATE = 20;

					private int flightTime;

					@Override
					public void onUpdate(final EntityPlayer player, final ItemStack stack) {
						if (flight.isFlying()) {
							if (this.flightTime++ % DAMAGE_RATE == (DAMAGE_RATE - 1)) {
								stack.damageItem(1, player);
							}
						} else {
							this.flightTime = 0;
						}
					}
				})
				.build()
			)
			.build());
		this.capabilities.accept(builder);
		return builder.build();
	}
}
