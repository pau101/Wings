package me.paulf.wings;

import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.ItemAccessor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.function.Consumer;

@Mod(modid = WingsMod.ID, useMetadata = true)
public final class WingsMod {
	public static final String ID = "wings";

	private static final class Holder {
		private static final WingsMod INSTANCE = new WingsMod();
	}

	@SidedProxy(
		clientSide = "me.paulf.wings.client.ClientProxy",
		serverSide = "me.paulf.wings.server.ServerProxy"
	)
	private static Proxy proxy;

	@Mod.EventHandler
	public void init(final FMLPreInitializationEvent event) {
		this.requireProxy().preinit();
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent event) {
		this.requireProxy().init();
	}

	public void addFlightListeners(final EntityPlayer player, final Flight instance) {
		this.requireProxy().addFlightListeners(player, instance);
	}

	public ItemAccessor<EntityLivingBase> getWingsAccessor() {
		return this.requireProxy().getWingsAccessor();
	}

	public Consumer<CapabilityProviders.CompositeBuilder> createAvianWings(final String name) {
		return this.requireProxy().createAvianWings(name);
	}

	public Consumer<CapabilityProviders.CompositeBuilder> createInsectoidWings(final String name) {
		return this.requireProxy().createInsectoidWings(name);
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
