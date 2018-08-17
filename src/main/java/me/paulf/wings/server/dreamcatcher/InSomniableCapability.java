package me.paulf.wings.server.dreamcatcher;

import me.paulf.wings.WingsMod;
import me.paulf.wings.util.CapabilityProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class InSomniableCapability {
	private InSomniableCapability() {}

	@CapabilityInject(InSomniable.class)
	private static final Capability<InSomniable> INSOMNIABLE = null;

	@CapabilityInject(Playable.class)
	private static final Capability<Playable> PLAYABLE = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(InSomniable.class, new Storage<>(), InSomniable::new);
		CapabilityManager.INSTANCE.register(Playable.class, new Storage<>(), Playable::new);
	}

	public static InSomniable get(EntityPlayer player) {
		if (player.hasCapability(INSOMNIABLE, null)) {
			return player.getCapability(INSOMNIABLE, null);
		}
		throw new IllegalStateException("Missing capability: " + player);
	}

	public static Playable get(TileEntityNote noteblock) {
		if (noteblock.hasCapability(PLAYABLE, null)) {
			return noteblock.getCapability(PLAYABLE, null);
		}
		throw new IllegalStateException("Missing capability: " + noteblock);
	}

	@SubscribeEvent
	public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (entity instanceof EntityPlayer) {
			event.addCapability(
				new ResourceLocation(WingsMod.ID, "insomniable"),
				CapabilityProviders.builder(INSOMNIABLE, new InSomniable())
					.serializedBy(new InSomniable.Serializer())
					.build()
			);
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		get(event.getEntityPlayer()).clone(get(event.getOriginal()));
	}

	@SubscribeEvent
	public static void onAttachBlockEntityCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
		TileEntity entity = event.getObject();
		if (entity instanceof TileEntityNote) {
			event.addCapability(
				new ResourceLocation(WingsMod.ID, "playable"),
				CapabilityProviders.builder(PLAYABLE, new Playable())
					.serializedBy(new Playable.Serializer())
					.build()
			);
		}
	}

	private static final class Storage<T> implements Capability.IStorage<T> {
		@Override
		public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
			return null;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {}
	}
}
