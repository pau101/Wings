package me.paulf.wings;

import me.paulf.wings.server.dreamcatcher.InSomniableCapability;
import me.paulf.wings.server.fix.WingsFixes;
import me.paulf.wings.server.flight.ConstructWingsAccessorEvent;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.FlightCapability;
import me.paulf.wings.server.net.Network;
import me.paulf.wings.server.net.clientbound.MessageSyncFlight;
import me.paulf.wings.util.ItemAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

public abstract class Proxy {
	protected final Network network = new Network();

	private ItemAccessor<EntityPlayer> wingsAccessor = ItemAccessor.none();

	public void preinit() {
		FlightCapability.register();
		InSomniableCapability.register();
		WingsFixes.register();
	}

	protected void init() {
		ConstructWingsAccessorEvent event = new ConstructWingsAccessorEvent();
		MinecraftForge.EVENT_BUS.post(event);
		wingsAccessor = event.build();
	}

	public void addFlightListeners(EntityPlayer player, Flight instance) {
		if (player instanceof EntityPlayerMP) {
			instance.registerFlyingListener(isFlying -> player.capabilities.allowFlying = isFlying);
			instance.registerFlyingListener(isFlying -> {
				if (isFlying) {
					player.dismountRidingEntity();
				}
			});
			Flight.Notifier notifier = Flight.Notifier.of(
				() -> network.sendToPlayer(new MessageSyncFlight(player, instance), (EntityPlayerMP) player),
				p -> network.sendToPlayer(new MessageSyncFlight(player, instance), p),
				() -> network.sendToAllTracking(new MessageSyncFlight(player, instance), player)
			);
			instance.registerSyncListener(players -> players.notify(notifier));
		}
	}

	public final ItemAccessor<EntityPlayer> getWingsAccessor() {
		return wingsAccessor;
	}
}
