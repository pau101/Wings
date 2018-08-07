package me.paulf.wings;

import baubles.api.render.IRenderBauble;
import me.paulf.wings.server.capability.Flight;
import me.paulf.wings.server.capability.FlightCapability;
import me.paulf.wings.server.fix.WingsFixes;
import me.paulf.wings.server.flight.FlightDefault;
import me.paulf.wings.server.item.StandardWing;
import me.paulf.wings.server.net.Network;
import me.paulf.wings.server.net.clientbound.MessageSyncFlight;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class Proxy {
	protected final Network network = new Network();

	public void preInit() {
		FlightCapability.register();
		WingsFixes.register();
	}

	public void init() {}

	public void renderWings(StandardWing type, EntityPlayer player, IRenderBauble.RenderType renderType, float delta) {}

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

	public final Network getNetwork() {
		return network;
	}
}
