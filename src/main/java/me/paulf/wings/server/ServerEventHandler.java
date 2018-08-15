package me.paulf.wings.server;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.flight.ConstructWingsAccessorEvent;
import me.paulf.wings.util.ItemPlacing;
import me.paulf.wings.server.asm.GetLivingHeadLimitEvent;
import me.paulf.wings.server.asm.PlayerFlightCheckEvent;
import me.paulf.wings.server.capability.Flight;
import me.paulf.wings.server.capability.FlightCapability;
import me.paulf.wings.server.item.WingsItems;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class ServerEventHandler {
	private ServerEventHandler() {}

	@SubscribeEvent
	public static void onInteract(PlayerInteractEvent.EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		EnumHand hand = event.getHand();
		ItemStack stack = player.getHeldItem(hand);
		if (event.getTarget() instanceof EntityBat && stack.getItem() == Items.GLASS_BOTTLE) {
			player.world.playSound(
				player,
				player.posX, player.posY, player.posZ,
				SoundEvents.ITEM_BOTTLE_FILL,
				SoundCategory.NEUTRAL,
				1.0F,
				1.0F
			);
			ItemStack destroyed = stack.copy();
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
			StatBase useStat = StatList.getObjectUseStats(Items.GLASS_BOTTLE);
			if (useStat != null) {
				player.addStat(useStat);
			}
			ItemStack batBlood = new ItemStack(WingsItems.BAT_BLOOD);
			if (stack.isEmpty()) {
				ForgeEventFactory.onPlayerDestroyItem(player, destroyed, hand);
				player.setHeldItem(hand, batBlood);
			} else if (!player.inventory.addItemStackToInventory(batBlood)) {
				player.dropItem(batBlood, false);
			}
			event.setCancellationResult(EnumActionResult.SUCCESS);
		}
	}

	@SubscribeEvent
	public static void onMount(EntityMountEvent event) {
		if (event.isMounting()) {
			FlightCapability.ifPlayer(event.getEntityMounting(), (player, flight) -> {
				if (flight.isFlying()) {
					event.setCanceled(true);
				}
			});
		}
	}

	@SubscribeEvent
	public static void onPlayerUpdate(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			FlightCapability.get(event.player).onUpdate(event.player);
		}
	}

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {
		FlightCapability.ifPlayer(event.getEntityLiving(), (player, flight) ->
			flight.setIsFlying(false, Flight.PlayerSet.ofAll())
		);
	}

	@SubscribeEvent
	public static void onFlightCheck(PlayerFlightCheckEvent event) {
		if (FlightCapability.get(event.getEntityPlayer()).isFlying()) {
			event.setResult(Event.Result.ALLOW);
		}
	}

	@SubscribeEvent
	public static void onGetLivingHeadLimit(GetLivingHeadLimitEvent event) {
		FlightCapability.ifPlayer(event.getEntityLiving(), (player, flight) -> {
			if (flight.isFlying()) {
				event.setHardLimit(50.0F);
				event.disableSoftLimit();
			}
		});
	}

	@SubscribeEvent
	public static void onConstructWingsAccessor(ConstructWingsAccessorEvent event) {
		event.addPlacing(ItemPlacing.forArmor(EntityEquipmentSlot.CHEST));
	}
}
