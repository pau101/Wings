package me.paulf.wings.client.flight;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.flight.AttachFlightCapabilityEvent;
import me.paulf.wings.util.CapabilityProviders;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = WingsMod.ID, value = Side.CLIENT)
public final class FlightViewCapability {
	private FlightViewCapability() {}

	private static final ResourceLocation FLIGHT_VIEW_ID = new ResourceLocation(WingsMod.ID, "flight_view");

	@CapabilityInject(FlightView.class)
	private static final Capability<FlightView> CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(FlightView.class, new EmptyStorage<>(), () -> {
			throw new UnsupportedOperationException();
		});
	}

	public static FlightView get(AbstractClientPlayer player) {
		if (player.hasCapability(CAPABILITY, null)) {
			return player.getCapability(CAPABILITY, null);
		}
		throw new IllegalStateException("Missing capability: " + player);
	}

	@SubscribeEvent
	public static void onAttachCapabilities(AttachFlightCapabilityEvent event) {
		Entity entity = event.getObject();
		if (entity instanceof AbstractClientPlayer) {
			event.addCapability(
				FLIGHT_VIEW_ID,
				CapabilityProviders.builder(CAPABILITY, new FlightViewDefault(event.getInstance())).build()
			);
		}
	}

	private static final class EmptyStorage<T> implements Capability.IStorage<T> {
		@Override
		public NBTTagCompound writeNBT(Capability<T> capability, T instance, EnumFacing side) {
			return null;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {}
	}
}
