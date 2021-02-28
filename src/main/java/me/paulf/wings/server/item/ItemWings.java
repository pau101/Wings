package me.paulf.wings.server.item;

import com.google.common.collect.ImmutableSet;
import me.paulf.wings.WingsMod;
import me.paulf.wings.server.apparatus.FlightApparatus;
import me.paulf.wings.server.apparatus.FlightApparatuses;
import me.paulf.wings.server.apparatus.SimpleFlightApparatus;
import me.paulf.wings.server.item.group.ItemGroupWings;
import me.paulf.wings.server.sound.WingsSounds;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.HandlerSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class ItemWings extends Item {
	private final ImmutableSet<EnchantmentType> allowedEnchantmentTypes;

	private final Consumer<CapabilityProviders.CompositeBuilder> capabilities;

	private WingSettings settings;

	private int maxDamage;

	private ItemWings(final Item.Properties properties, final ImmutableSet<EnchantmentType> allowedEnchantmentTypes, final Consumer<CapabilityProviders.CompositeBuilder> capabilities, final WingSettings settings) {
		super(properties);
		this.allowedEnchantmentTypes = allowedEnchantmentTypes;
		this.capabilities = capabilities;
		this.settings = settings;
	}

	public void setSettings(final WingSettings settings) {
		this.settings = settings;
		this.setMaxDamage(settings.getItemDurability());
	}

	private void setMaxDamage(final int maxDamage) {
		this.maxDamage = maxDamage;
	}

	@Override
	public int getMaxDamage(final ItemStack stack) {
		return this.maxDamage;
	}

	@Override
	public boolean isDamageable() {
		return this.maxDamage > 0;
	}

	@Override
	public EquipmentSlotType getEquipmentSlot(final ItemStack stack) {
		return EquipmentSlotType.CHEST;
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
		return false;//WingsDict.test(ingredient, WingsDict.FAIRY_DUST); FIXME
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		for (final HandlerSlot slot : WingsMod.instance().getWingsAccessor().enumerate(player)) {
			final ItemStack split = stack.split(1);
			final ItemStack remaining = slot.insert(split);
			stack.grow(remaining.getCount());
			if (remaining.getCount() < split.getCount()) {
				player.playSound(WingsSounds.ITEM_ARMOR_EQUIP_WINGS.get(), 1.0F, 1.0F);
				return new ActionResult<>(ActionResultType.SUCCESS, stack);
			}
		}
		return new ActionResult<>(ActionResultType.FAIL, stack);
	}

	public static ItemWings create(final Consumer<CapabilityProviders.CompositeBuilder> capabilities, final WingSettings attributes) {
		final ItemWings wings = new ItemWings(
			new Item.Properties().maxStackSize(1).group(ItemGroupWings.instance()),
			ImmutableSet.of(
				EnchantmentType.BREAKABLE,
				EnchantmentType.WEARABLE
			),
			capabilities,
			attributes
		);
		wings.setMaxDamage(attributes.getItemDurability());
		return wings;
	}

	@Override
	public ICapabilityProvider initCapabilities(final ItemStack stack, @Nullable final CompoundNBT tag) {
		final CapabilityProviders.CompositeBuilder builder = CapabilityProviders.builder()
			.add(FlightApparatuses.providerBuilder(SimpleFlightApparatus.builder()
				.withFlight(((player, wings, direction) -> {
					int distance = Math.round((float) direction.length() * 100.0F);
					if (distance > 0) {
						player.addExhaustion(distance * this.settings.getFlyingExertion());
					}
				}))
				.withLanding(((player, wings, direction) -> player.addExhaustion(this.settings.getLandingExertion())))
				.withUsability((player, wings) -> (!wings.isDamageable() || wings.getDamage() < wings.getMaxDamage() - 1) && player.getFoodStats().getFoodLevel() >= this.settings.getRequiredFlightSatiation())
				.withLandability((player, wings) -> player.getFoodStats().getFoodLevel() >= this.settings.getRequiredLandSatiation())
				.withVitality(flight -> new FlightApparatus.FlightState() {
					private static final int DAMAGE_RATE = 20;

					private int flightTime;

					@Override
					public void onUpdate(final PlayerEntity player, final ItemStack stack) {
						if (flight.isFlying()) {
							if (this.flightTime++ % DAMAGE_RATE == (DAMAGE_RATE - 1)) {
								stack.damageItem(1, player, p -> p.sendBreakAnimation(EquipmentSlotType.CHEST));
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
