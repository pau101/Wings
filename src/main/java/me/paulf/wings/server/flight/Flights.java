package me.paulf.wings.server.flight;

import me.paulf.wings.WingsMod;
import me.paulf.wings.util.CapabilityHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class Flights {
	private Flights() {}

	private static final CapabilityHolder<PlayerEntity, Flight, CapabilityHolder.State<PlayerEntity, Flight>> HOLDER = CapabilityHolder.create();

	public static boolean has(final PlayerEntity player) {
		return HOLDER.state().has(player, null);
	}

	public static LazyOptional<Flight> get(final PlayerEntity player) {
		return HOLDER.state().get(player, null);
	}

	@CapabilityInject(Flight.class)
	static void inject(final Capability<Flight> capability) {
		HOLDER.inject(capability);
	}

	public static void ifPlayer(final Entity entity, final BiConsumer<PlayerEntity, Flight> action) {
		ifPlayer(entity, e -> true, action);
	}

	public static void ifPlayer(final Entity entity, final Predicate<PlayerEntity> condition, final BiConsumer<PlayerEntity, Flight> action) {
		if (entity instanceof PlayerEntity) {
			final PlayerEntity player = (PlayerEntity) entity;
			get(player).filter(f -> condition.test(player))
				.ifPresent(f -> action.accept(player, f));
		}
	}

	@SubscribeEvent
	public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
		final Entity entity = event.getObject();
		if (entity instanceof PlayerEntity) {
			final Supplier<FlightDefault> factory = () -> {
				final FlightDefault flight = new FlightDefault();
				WingsMod.instance().addFlightListeners((PlayerEntity) entity, flight);
				return flight;
			};
			final FlightDefault flight = factory.get();
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
	public static void onPlayerClone(final PlayerEvent.Clone event) {
		if (!event.isWasDeath()) {
			get(event.getOriginal()).ifPresent(oldInstance ->
				get(event.getPlayer()).ifPresent(newInstance -> newInstance.clone(oldInstance))
			);
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(final PlayerEvent.PlayerRespawnEvent event) {
		get(event.getPlayer()).ifPresent(flight -> flight.sync(Flight.PlayerSet.ofSelf()));
	}

	@SubscribeEvent
	public static void onPlayerChangedDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
		get(event.getPlayer()).ifPresent(flight -> flight.sync(Flight.PlayerSet.ofSelf()));
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
		get(event.getPlayer()).ifPresent(flight -> flight.sync(Flight.PlayerSet.ofSelf()));
	}

	@SubscribeEvent
	public static void onPlayerStartTracking(final PlayerEvent.StartTracking event) {
		ifPlayer(event.getTarget(), (player, flight) ->
			flight.sync(Flight.PlayerSet.ofPlayer((ServerPlayerEntity) event.getPlayer()))
		);
	}
}
