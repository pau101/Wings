package me.paulf.wings;

import me.paulf.wings.server.capability.Flight;
import me.paulf.wings.server.capability.FlightCapability;
import me.paulf.wings.server.fix.WingsFixes;
import me.paulf.wings.server.flight.FlightDefault;
import me.paulf.wings.server.net.Network;
import me.paulf.wings.server.net.clientbound.MessageSyncFlight;
import me.paulf.wings.util.ItemAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

public abstract class Proxy {
	protected final Network network = new Network();

	private final WingsProvider wingsProvider = new WingsProvider();

	private final ItemAccessor<EntityPlayer> wingsAccessor = createWingsAccessor(wingsProvider);

	protected void preInit() {
		FlightCapability.register();
		WingsFixes.register();
		wingsProvider.addEventListeners(MinecraftForge.EVENT_BUS::register);
	}

	protected void init() {}

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

	private static ItemAccessor<EntityPlayer> createWingsAccessor(WingsProvider provider) {
		ItemAccessor.Builder<EntityPlayer> bob = ItemAccessor.builder();
		provider.addEquipmentPlacings(bob::withPlacing);
		return bob.build();
	}
}
