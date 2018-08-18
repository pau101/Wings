package me.paulf.wings;

import me.paulf.wings.server.dreamcatcher.InSomniableCapability;
import me.paulf.wings.server.fix.WingsFixes;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.ConstructWingsAccessorEvent;
import me.paulf.wings.server.flight.FlightCapability;
import me.paulf.wings.server.flight.FlightDefault;
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

	public final Flight newFlight(EntityPlayer player) {
		Flight flight = new FlightDefault();
		if (player instanceof EntityPlayerMP) {
			flight.registerFlyingListener(isFlying -> player.capabilities.allowFlying = isFlying);
			flight.registerFlyingListener(isFlying -> {
				if (isFlying) {
					player.dismountRidingEntity();
				}
			});
			Flight.Notifier notifier = Flight.Notifier.of(
				() -> network.sendToPlayer(new MessageSyncFlight(player, flight), (EntityPlayerMP) player),
				p -> network.sendToPlayer(new MessageSyncFlight(player, flight), p),
				() -> network.sendToAllTracking(new MessageSyncFlight(player, flight), player)
			);
			flight.registerSyncListener(players -> players.notify(notifier));
		}
		addFlightListeners(player, flight);
		return flight;
	}

	protected abstract void addFlightListeners(EntityPlayer player, Flight flight);

	public final ItemAccessor<EntityPlayer> getWingsAccessor() {
		return wingsAccessor;
	}
}
