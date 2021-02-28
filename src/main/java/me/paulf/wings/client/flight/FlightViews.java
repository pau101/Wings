package me.paulf.wings.client.flight;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.flight.AttachFlightCapabilityEvent;
import me.paulf.wings.util.CapabilityHolder;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = WingsMod.ID, value = Dist.CLIENT)
public final class FlightViews {
	private FlightViews() {}

	private static final CapabilityHolder<LivingEntity, FlightView, CapabilityHolder.State<LivingEntity, FlightView>> HOLDER = CapabilityHolder.create();

	public static boolean has(final LivingEntity player) {
		return HOLDER.state().has(player, null);
	}

	public static LazyOptional<FlightView> get(final LivingEntity player) {
		return HOLDER.state().get(player, null);
	}

	@CapabilityInject(FlightView.class)
	static void inject(final Capability<FlightView> capability) {
		HOLDER.inject(capability);
	}

	@SubscribeEvent
	public static void onAttachCapabilities(final AttachFlightCapabilityEvent event) {
		final Entity entity = event.getObject();
		if (entity instanceof ClientPlayerEntity) {
			event.addCapability(
				new ResourceLocation(WingsMod.ID, "flight_view"),
				HOLDER.state().providerBuilder(new FlightViewDefault((PlayerEntity) entity, event.getInstance())).build()
			);
		}
	}

	@SubscribeEvent
	public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
		final Entity entity = event.getObject();
		if (entity instanceof LivingEntity && !(entity instanceof ClientPlayerEntity)) {
			event.addCapability(
				new ResourceLocation(WingsMod.ID, "flight_view"),
				HOLDER.state().providerBuilder(new FlightViewStatic((LivingEntity) entity)).build()
			);
		}
	}
}
