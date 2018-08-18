package me.paulf.wings.server.flight;

import me.paulf.wings.WingsMod;
import me.paulf.wings.util.CapabilityProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
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

import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class FlightCapability {
	private FlightCapability() {}

	private static final ResourceLocation FLIGHT_ID = new ResourceLocation(WingsMod.ID, "flight");

	@CapabilityInject(Flight.class)
	private static final Capability<Flight> CAPABILITY = null;

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

	public static void ifPlayer(Entity entity, Predicate<EntityPlayer> condition, BiConsumer<EntityPlayer, Flight> action) {
		EntityPlayer player;
		if (entity instanceof EntityPlayer && condition.test(player = (EntityPlayer) entity)) {
			action.accept(player, get(player));
		}
	}

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		Entity entity = event.getObject();
		if (entity instanceof EntityPlayer) {
			Flight flight = WingsMod.instance().newFlight((EntityPlayer) entity);
			event.addCapability(
				FLIGHT_ID,
				CapabilityProviders.builder(CAPABILITY, flight).build()
			);
			MinecraftForge.EVENT_BUS.post(AttachFlightCapabilityEvent.create(event, flight));
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		get(event.getEntityPlayer()).clone(get(event.getOriginal()), Flight.PlayerSet.empty());
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerRespawnEvent event) {
		get(event.player).sync(Flight.PlayerSet.ofSelf());
	}

	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		get(event.player).sync(Flight.PlayerSet.ofSelf());
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		get(event.player).sync(Flight.PlayerSet.ofSelf());
	}

	@SubscribeEvent
	public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
		ifPlayer(event.getTarget(), (player, flight) ->
			flight.sync(Flight.PlayerSet.ofPlayer((EntityPlayerMP) event.getEntityPlayer()))
		);
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
