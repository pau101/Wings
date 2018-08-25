package me.paulf.wings;

import me.paulf.wings.server.dreamcatcher.InSomniable;
import me.paulf.wings.server.dreamcatcher.Playable;
import me.paulf.wings.server.fix.WingsFixes;
import me.paulf.wings.server.flight.ConstructWingsAccessorEvent;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.FlightDefault;
import me.paulf.wings.server.net.Network;
import me.paulf.wings.server.net.clientbound.MessageSyncFlight;
import me.paulf.wings.server.apparatus.SimpleFlightApparatus;
import me.paulf.wings.server.apparatus.FlightApparatus;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.ItemAccessor;
import me.paulf.wings.util.SimpleStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.function.Consumer;

public abstract class Proxy {
	protected final Network network = new Network();

	private ItemAccessor<EntityPlayer> wingsAccessor = ItemAccessor.none();

	public void preinit() {
		CapabilityManager.INSTANCE.register(Flight.class, SimpleStorage.ofVoid(), FlightDefault::new);
		CapabilityManager.INSTANCE.register(InSomniable.class, SimpleStorage.ofVoid(), InSomniable::new);
		CapabilityManager.INSTANCE.register(Playable.class, SimpleStorage.ofVoid(), Playable::new);
		CapabilityManager.INSTANCE.register(FlightApparatus.class, SimpleStorage.ofVoid(), SimpleFlightApparatus.builder()::build);
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

	public abstract Consumer<CapabilityProviders.CompositeBuilder> createAvianWings(String name);

	public abstract Consumer<CapabilityProviders.CompositeBuilder> createInsectoidWings(String name);
}
