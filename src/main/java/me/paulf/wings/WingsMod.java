package me.paulf.wings;

import me.paulf.wings.client.ClientProxy;
import me.paulf.wings.server.ServerProxy;
import me.paulf.wings.server.block.WingsBlocks;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.item.WingsItems;
import me.paulf.wings.server.sound.WingsSounds;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.ItemAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

	private Proxy proxy;

	public WingsMod() {
		if (INSTANCE != null) throw new IllegalStateException("Already constructed!");
		INSTANCE = this;
		final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		WingsBlocks.REG.register(bus);
		WingsItems.REG.register(bus);
		WingsSounds.REG.register(bus);
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
