package me.paulf.wings.server;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.apparatus.FlightApparatuses;
import me.paulf.wings.server.asm.GetLivingHeadLimitEvent;
import me.paulf.wings.server.asm.PlayerFlightCheckEvent;
import me.paulf.wings.server.asm.PlayerFlownEvent;
import me.paulf.wings.server.flight.ConstructWingsAccessorEvent;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.Flights;
import me.paulf.wings.server.item.WingsItems;
import me.paulf.wings.util.ItemPlacing;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class ServerEventHandler {
	private ServerEventHandler() {}

	@SubscribeEvent
	public static void onPlayerEntityInteract(final PlayerInteractEvent.EntityInteract event) {
		final PlayerEntity player = event.getPlayer();
		final Hand hand = event.getHand();
		final ItemStack stack = player.getItemInHand(hand);
		if (event.getTarget() instanceof BatEntity && stack.getItem() == Items.GLASS_BOTTLE) {
			player.level.playSound(
				player,
				player.getX(), player.getY(), player.getZ(),
				SoundEvents.BOTTLE_FILL,
				SoundCategory.NEUTRAL,
				1.0F,
				1.0F
			);
			final ItemStack destroyed = stack.copy();
			if (!player.abilities.instabuild) {
				stack.shrink(1);
			}
			player.awardStat(Stats.ITEM_USED.get(Items.GLASS_BOTTLE));
			final ItemStack batBlood = new ItemStack(WingsItems.BAT_BLOOD.get());
			if (stack.isEmpty()) {
				ForgeEventFactory.onPlayerDestroyItem(player, destroyed, hand);
				player.setItemInHand(hand, batBlood);
			} else if (!player.inventory.add(batBlood)) {
				player.drop(batBlood, false);
			}
			event.setCancellationResult(ActionResultType.SUCCESS);
		}
	}

	@SubscribeEvent
	public static void onEntityMount(final EntityMountEvent event) {
		if (event.isMounting()) {
			Flights.ifPlayer(event.getEntityMounting(), (player, flight) -> {
				if (flight.isFlying()) {
					event.setCanceled(true);
				}
			});
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Flights.get(event.player).ifPresent(flight ->
				flight.tick(event.player, FlightApparatuses.find(event.player))
			);
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(final LivingDeathEvent event) {
		Flights.ifPlayer(event.getEntityLiving(), (player, flight) ->
			flight.setIsFlying(false, Flight.PlayerSet.ofAll())
		);
	}

	@SubscribeEvent
	public static void onPlayerFlightCheck(final PlayerFlightCheckEvent event) {
		Flights.get(event.getPlayer()).filter(Flight::isFlying)
			.ifPresent(flight -> event.setFlying());
	}

	@SubscribeEvent
	public static void onPlayerFlown(final PlayerFlownEvent event) {
		final PlayerEntity player = event.getPlayer();
		Flights.get(player).ifPresent(flight -> {
			flight.onFlown(player, FlightApparatuses.find(event.getPlayer()), event.getDirection());
		});
	}

	@SubscribeEvent
	public static void onGetLivingHeadLimit(final GetLivingHeadLimitEvent event) {
		Flights.ifPlayer(event.getEntityLiving(), (player, flight) -> {
			if (flight.isFlying()) {
				event.setHardLimit(50.0F);
				event.disableSoftLimit();
			}
		});
	}
}
