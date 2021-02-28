package me.paulf.wings.server.dreamcatcher;

import me.paulf.wings.WingsMod;
import me.paulf.wings.util.CapabilityHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class InSomniableCapability {
	private InSomniableCapability() {}

	private static final CapabilityHolder<PlayerEntity, InSomniable, CapabilityHolder.State<PlayerEntity, InSomniable>> INSOMNIABLE = CapabilityHolder.create();

	public static LazyOptional<InSomniable> getInSomniable(final PlayerEntity player) {
		return INSOMNIABLE.state().get(player, null);
	}

	@CapabilityInject(InSomniable.class)
	static void injectInSomniable(final Capability<InSomniable> capability) {
		INSOMNIABLE.inject(capability);
	}

	@SubscribeEvent
	public static void onAttachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event) {
		final Entity entity = event.getObject();
		if (entity instanceof PlayerEntity) {
			event.addCapability(
				new ResourceLocation(WingsMod.ID, "insomniable"),
				INSOMNIABLE.state().providerBuilder(new InSomniable())
					.serializedBy(new InSomniable.Serializer())
					.build()
			);
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(final PlayerEvent.Clone event) {
		getInSomniable(event.getOriginal())
			.ifPresent(oldInstance -> getInSomniable(event.getPlayer())
				.ifPresent(newInstance -> newInstance.clone(oldInstance))
			);
	}
}
