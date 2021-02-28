package me.paulf.wings;

import me.paulf.wings.server.apparatus.FlightApparatus;
import me.paulf.wings.server.apparatus.SimpleFlightApparatus;
import me.paulf.wings.server.config.WingsItemsConfig;
import me.paulf.wings.server.dreamcatcher.InSomniable;
import me.paulf.wings.server.flight.ConstructWingsAccessorEvent;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.FlightDefault;
import me.paulf.wings.server.net.Network;
import me.paulf.wings.server.net.clientbound.MessageSetWingSettings;
import me.paulf.wings.server.net.clientbound.MessageSyncFlight;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.ItemAccessor;
import me.paulf.wings.util.ItemPlacing;
import me.paulf.wings.util.SimpleStorage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;

public abstract class Proxy {
	protected final Network network = new Network();

	private ItemAccessor<LivingEntity> wingsAccessor = ItemAccessor.none();

	public void init(final IEventBus modBus) {
		modBus.addListener(this::setup);
		MinecraftForge.EVENT_BUS.<PlayerEvent.PlayerLoggedInEvent>addListener(e ->
			this.network.sendToPlayer(new MessageSetWingSettings(WingsItemsConfig.createWingAttributes()), (ServerPlayerEntity) e.getPlayer())
		);
		modBus.<ConstructWingsAccessorEvent>addListener(e -> e.addPlacing(ItemPlacing.forArmor(EquipmentSlotType.CHEST)));
		modBus.<FMLLoadCompleteEvent>addListener(e -> this.lateinit());
	}

	protected void setup(final FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(Flight.class, SimpleStorage.ofVoid(), FlightDefault::new);
		CapabilityManager.INSTANCE.register(FlightApparatus.class, SimpleStorage.ofVoid(), SimpleFlightApparatus.builder()::build);
		CapabilityManager.INSTANCE.register(InSomniable.class, SimpleStorage.ofVoid(), InSomniable::new);
	}

	protected void lateinit() {
		final ConstructWingsAccessorEvent event = new ConstructWingsAccessorEvent();
		FMLJavaModLoadingContext.get().getModEventBus().post(event);
		this.wingsAccessor = event.build();
	}

	public void addFlightListeners(final PlayerEntity player, final Flight instance) {
		if (player instanceof ServerPlayerEntity) {
			instance.registerFlyingListener(isFlying -> player.abilities.allowFlying = isFlying);
			instance.registerFlyingListener(isFlying -> {
				if (isFlying) {
					player.dismount();
				}
			});
			final Flight.Notifier notifier = Flight.Notifier.of(
				() -> this.network.sendToPlayer(new MessageSyncFlight(player, instance), (ServerPlayerEntity) player),
				p -> this.network.sendToPlayer(new MessageSyncFlight(player, instance), p),
				() -> this.network.sendToAllTracking(new MessageSyncFlight(player, instance), player)
			);
			instance.registerSyncListener(players -> players.notify(notifier));
		}
	}

	public final ItemAccessor<LivingEntity> getWingsAccessor() {
		return this.wingsAccessor;
	}

	public abstract Consumer<CapabilityProviders.CompositeBuilder> createAvianWings(String name);

	public abstract Consumer<CapabilityProviders.CompositeBuilder> createInsectoidWings(String name);
}
