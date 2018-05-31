package com.pau101.wings.server.capability;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.pau101.wings.WingsMod;
import com.pau101.wings.server.flight.FlightDefault;
import com.pau101.wings.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class FlightCapability {
	private FlightCapability() {}

	private static final ResourceLocation FLIGHT_ID = new ResourceLocation(WingsMod.ID, "flight");

	@CapabilityInject(Flight.class)
	public static final Capability<Flight> CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(Flight.class, new FlightStorage(), FlightDefault::new);
	}

	public static Flight get(EntityPlayer player) {
		if (player.hasCapability(CAPABILITY, null)) {
			return player.getCapability(CAPABILITY, null);
		}
		throw new IllegalStateException("Missing capability: " + player);
	}

	public static void ifPlayer(Entity entity, BiConsumer<EntityPlayer, Flight> action) {
		ifPlayer(entity, e -> true, action);
	}

	public static void ifPlayer(Entity entity, Predicate<Entity> condition, BiConsumer<EntityPlayer, Flight> action) {
		Util.ifPlayer(entity, condition, player -> action.accept(player, get(player)));
	}

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		Util.ifPlayer(event.getObject(), player -> event.addCapability(FLIGHT_ID, WingsMod.instance().newFlight(player)));
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		get(event.getEntityPlayer()).clone(get(event.getOriginal()), Flight.PlayerSet.ALL);
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerRespawnEvent event) {
		sync(event.player);
	}

	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		sync(event.player);
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		sync(event.player);
	}

	private static void sync(EntityPlayer player) {
		get(player).sync(Flight.PlayerSet.ALL);
	}

	private static final class FlightStorage implements Capability.IStorage<Flight> {
		@Override
		public NBTTagCompound writeNBT(Capability<Flight> capability, Flight instance, EnumFacing side) {
			return instance.serializeNBT();
		}

		@Override
		public void readNBT(Capability<Flight> capability, Flight instance, EnumFacing side, NBTBase nbt) {
			instance.deserializeNBT(nbt instanceof NBTTagCompound ? (NBTTagCompound) nbt : new NBTTagCompound());
		}
	}
}
