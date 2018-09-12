package me.paulf.wings;

import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.ItemAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.function.Consumer;

@Mod(
	modid = WingsMod.ID,
	name = WingsMod.NAME,
	version = WingsMod.VERSION,
	dependencies = "required-after:forge@[14.23.4.2705,);required-after:llibrary@[1.7,1.8)"
)
public final class WingsMod {
	public static final String ID = "wings";

	public static final String NAME = "Wings";

	public static final String VERSION = "1.0.5";

	private static final class Holder {
		private static final WingsMod INSTANCE = new WingsMod();
	}

	@SidedProxy(
		clientSide = "me.paulf.wings.client.ClientProxy",
		serverSide = "me.paulf.wings.server.ServerProxy"
	)
	private static Proxy proxy;

	@Mod.EventHandler
	public void init(FMLPreInitializationEvent event) {
		requireProxy().preinit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		requireProxy().init();
	}

	public void addFlightListeners(EntityPlayer player, Flight instance) {
		requireProxy().addFlightListeners(player, instance);
	}

	public ItemAccessor<EntityPlayer> getWingsAccessor() {
		return requireProxy().getWingsAccessor();
	}

	public Consumer<CapabilityProviders.CompositeBuilder> createAvianWings(String name) {
		return requireProxy().createAvianWings(name);
	}

	public Consumer<CapabilityProviders.CompositeBuilder> createInsectoidWings(String name) {
		return requireProxy().createInsectoidWings(name);
	}

	@Mod.InstanceFactory
	public static WingsMod instance() {
		return Holder.INSTANCE;
	}

	private Proxy requireProxy() {
		if (proxy == null) {
			throw new IllegalStateException("Proxy not initialized");
		}
		return proxy;
	}
}
