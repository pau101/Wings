package me.paulf.wings.client.flight;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.flight.AttachFlightCapabilityEvent;
import me.paulf.wings.util.CapabilityHolder;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = WingsMod.ID, value = Side.CLIENT)
public final class FlightViews {
	private FlightViews() {}

	private static final CapabilityHolder<EntityLivingBase, FlightView, CapabilityHolder.State<EntityLivingBase, FlightView>> HOLDER = CapabilityHolder.create();

	public static boolean has(final EntityLivingBase player) {
		return HOLDER.state().has(player, null);
	}

	@Nullable
	public static FlightView get(final EntityLivingBase player) {
		return HOLDER.state().get(player, null);
	}

	@CapabilityInject(FlightView.class)
	static void inject(final Capability<FlightView> capability) {
		HOLDER.inject(capability);
	}

	@SubscribeEvent
	public static void onAttachCapabilities(final AttachFlightCapabilityEvent event) {
		final Entity entity = event.getObject();
		if (entity instanceof AbstractClientPlayer) {
			event.addCapability(
				new ResourceLocation(WingsMod.ID, "flight_view"),
				HOLDER.state().providerBuilder(new FlightViewDefault((EntityPlayer) entity, event.getInstance())).build()
			);
		}
	}

	@SubscribeEvent
	public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
		final Entity entity = event.getObject();
		if (entity instanceof EntityLivingBase && !(entity instanceof AbstractClientPlayer)) {
			event.addCapability(
				new ResourceLocation(WingsMod.ID, "flight_view"),
				HOLDER.state().providerBuilder(new FlightViewStatic((EntityLivingBase) entity)).build()
			);
		}
	}
}
