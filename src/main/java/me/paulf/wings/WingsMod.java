package me.paulf.wings;

import com.mojang.serialization.Lifecycle;
import me.paulf.wings.client.ClientProxy;
import me.paulf.wings.server.ServerProxy;
import me.paulf.wings.server.apparatus.FlightApparatus;
import me.paulf.wings.server.apparatus.SimpleFlightApparatus;
import me.paulf.wings.server.config.WingsItemsConfig;
import me.paulf.wings.server.effect.WingsEffects;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.item.WingsItems;
import me.paulf.wings.server.sound.WingsSounds;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.ItemAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Consumer;

@Mod(WingsMod.ID)
public final class WingsMod {
	public static final String ID = "wings";

	private static WingsMod INSTANCE;

	public static final Registry<FlightApparatus> WINGS = new DefaultedRegistry<>(ID + ":angel_wings", RegistryKey.getOrCreateRootKey(new ResourceLocation(ID, "wings")), Lifecycle.experimental());

	public static final FlightApparatus ANGEL_WINGS = Registry.register(WINGS, ID + ":angel_wings", new SimpleFlightApparatus(WingsItemsConfig.ANGEL));
	public static final FlightApparatus BAT_WINGS = Registry.register(WINGS, ID + ":bat_wings", new SimpleFlightApparatus(WingsItemsConfig.BAT));

	private Proxy proxy;

	public WingsMod() {
		if (INSTANCE != null) throw new IllegalStateException("Already constructed!");
		INSTANCE = this;
		final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		WingsItems.REG.register(bus);
		WingsSounds.REG.register(bus);
		WingsEffects.REG.register(bus);
		this.proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
		this.proxy.init(bus);
	}

	public void addFlightListeners(final PlayerEntity player, final Flight instance) {
		this.requireProxy().addFlightListeners(player, instance);
	}

	public ItemAccessor<LivingEntity> getWingsAccessor() {
		return this.requireProxy().getWingsAccessor();
	}

	public Consumer<CapabilityProviders.CompositeBuilder> createAvianWings(final String name) {
		return this.requireProxy().createAvianWings(name);
	}

	public Consumer<CapabilityProviders.CompositeBuilder> createInsectoidWings(final String name) {
		return this.requireProxy().createInsectoidWings(name);
	}

	public static WingsMod instance() {
		return INSTANCE;
	}

	private Proxy requireProxy() {
		if (this.proxy == null) {
			throw new IllegalStateException("Proxy not initialized");
		}
		return this.proxy;
	}
}
