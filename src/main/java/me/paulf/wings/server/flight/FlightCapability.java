package me.paulf.wings.server.flight;

import me.paulf.wings.WingsMod;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.SimpleStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class FlightCapability {
	private FlightCapability() {}

	private static final ResourceLocation FLIGHT_ID = new ResourceLocation(WingsMod.ID, "flight");

	@CapabilityInject(Flight.class)
	private static final Capability<Flight> CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(Flight.class, SimpleStorage.ofVoid(), FlightDefault::new);
	}

	public static Flight get(EntityPlayer player) {
		if (player.hasCapability(CAPABILITY, null)) {
			return player.getCapability(CAPABILITY, null);
		}
		throw new IllegalStateException("Missing capability: " + player);
	}

	public static void ifPlayer(Entity entity, BiConsumer<EntityPlayer, Flight> action) {
		ifPlayer(entity, e -> true, action);
	}

	public static void ifPlayer(Entity entity, Predicate<EntityPlayer> condition, BiConsumer<EntityPlayer, Flight> action) {
		EntityPlayer player;
		if (entity instanceof EntityPlayer && condition.test(player = (EntityPlayer) entity)) {
			action.accept(player, get(player));
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
				FLIGHT_ID,
				CapabilityProviders.builder(CAPABILITY, flight)
					.serializedBy(new FlightDefault.Serializer(factory))
					.build()
			);
			MinecraftForge.EVENT_BUS.post(AttachFlightCapabilityEvent.create(event, flight));
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		get(event.getEntityPlayer()).clone(get(event.getOriginal()), Flight.PlayerSet.empty());
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerRespawnEvent event) {
		get(event.player).sync(Flight.PlayerSet.ofSelf());
	}

	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		get(event.player).sync(Flight.PlayerSet.ofSelf());
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		get(event.player).sync(Flight.PlayerSet.ofSelf());
	}

	@SubscribeEvent
	public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
		ifPlayer(event.getTarget(), (player, flight) ->
			flight.sync(Flight.PlayerSet.ofPlayer((EntityPlayerMP) event.getEntityPlayer()))
		);
	}
}
