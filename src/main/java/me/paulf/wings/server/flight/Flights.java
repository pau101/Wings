package me.paulf.wings.server.flight;

import me.paulf.wings.WingsMod;
import me.paulf.wings.util.CapabilityHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class Flights {
	private Flights() {}

	private static final CapabilityHolder<EntityPlayer, Flight, CapabilityHolder.State<EntityPlayer, Flight>> HOLDER = CapabilityHolder.create();

	public static boolean has(EntityPlayer player) {
		return HOLDER.state().has(player, null);
	}

	@Nullable
	public static Flight get(EntityPlayer player) {
		return HOLDER.state().get(player, null);
	}

	@CapabilityInject(Flight.class)
	static void inject(Capability<Flight> capability) {
		HOLDER.inject(capability);
	}

	public static void ifPlayer(Entity entity, BiConsumer<EntityPlayer, Flight> action) {
		ifPlayer(entity, e -> true, action);
	}

	public static void ifPlayer(Entity entity, Predicate<EntityPlayer> condition, BiConsumer<EntityPlayer, Flight> action) {
		EntityPlayer player;
		Flight flight;
		if (entity instanceof EntityPlayer && (flight = get(player = (EntityPlayer) entity)) != null && condition.test(player)) {
			action.accept(player, flight);
		}
	}

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (entity instanceof EntityPlayer) {
			Supplier<FlightDefault> factory = () -> {
				FlightDefault flight = new FlightDefault();
				WingsMod.instance().addFlightListeners((EntityPlayer) entity, flight);
				return flight;
			};
			FlightDefault flight = factory.get();
			event.addCapability(
				new ResourceLocation(WingsMod.ID, "flight"),
				HOLDER.state().providerBuilder(flight)
					.serializedBy(new FlightDefault.Serializer(factory))
					.build()
			);
			MinecraftForge.EVENT_BUS.post(AttachFlightCapabilityEvent.create(event, flight));
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		Flight oldInstance = get(event.getOriginal()), newInstance;
		if (oldInstance != null && (newInstance = get(event.getEntityPlayer())) != null) {
			oldInstance.clone(newInstance);
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerRespawnEvent event) {
		Flight flight = get(event.player);
		if (flight != null) {
			flight.sync(Flight.PlayerSet.ofSelf());
		}
	}

	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		Flight flight = get(event.player);
		if (flight != null) {
			flight.sync(Flight.PlayerSet.ofSelf());
		}
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		Flight flight = get(event.player);
		if (flight != null) {
			flight.sync(Flight.PlayerSet.ofSelf());
		}
	}

	@SubscribeEvent
	public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
		ifPlayer(event.getTarget(), (player, flight) ->
			flight.sync(Flight.PlayerSet.ofPlayer((EntityPlayerMP) event.getEntityPlayer()))
		);
	}
}
