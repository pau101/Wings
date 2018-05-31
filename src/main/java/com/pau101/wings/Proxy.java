package com.pau101.wings;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.render.IRenderBauble;
import com.pau101.wings.server.capability.Flight;
import com.pau101.wings.server.capability.FlightCapability;
import com.pau101.wings.server.flight.FlightDefault;
import com.pau101.wings.server.net.Network;
import com.pau101.wings.server.net.clientbound.MessageSyncFlight;
import com.pau101.wings.util.Mth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.ListenerList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public abstract class Proxy {
	protected final Network network = new Network();

	public void preInit() {
		FlightCapability.register();
		try {
			hackBaubles();
		} catch (Exception e) {
			throw new RuntimeException("Unable to hook baubles", e);
		}
	}

	private void hackBaubles() throws Exception {
		ListenerList list = new PlayerDropsEvent(null, DamageSource.GENERIC, null, false).getListenerList();
		int id = ReflectionHelper.getPrivateValue(EventBus.class, MinecraftForge.EVENT_BUS, "busID");
		Stream.of(list.getListeners(id))
			.filter(el -> Objects.toString(el).matches("[^,]+, ASM: baubles\\.common\\.event\\.EventHandlerEntity[^ ]+ playerDeath\\(Lnet/minecraftforge/event/entity/player/PlayerDropsEvent;\\)V"))
			.findFirst()
			.ifPresent(el -> {
				list.unregister(id, el);
				MinecraftForge.EVENT_BUS.register(new Object() {
					@SubscribeEvent
					public void onCollectPlayerDrops(PlayerDropsEvent event) {
						if (event.getEntity() instanceof EntityPlayer && !event.getEntity().world.isRemote && !event.getEntity().world.getGameRules().getBoolean("keepInventory")) {
							addItems(event.getEntityPlayer(), event.getDrops(), event.getEntityPlayer());
						}
					}

					private void addItems(EntityPlayer player, List<EntityItem> drops, Entity entity) {
						IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
						for (int i = 0; i < baubles.getSlots(); i++) {
							ItemStack stack = baubles.getStackInSlot(i);
							if (!stack.isEmpty()) {
								EntityItem ei = new EntityItem(entity.world, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ, stack.copy());
								ei.setPickupDelay(40);
								float magnitude = entity.world.rand.nextFloat() * 0.5F;
								float theta = entity.world.rand.nextFloat() * Mth.PI * 2.0F;
								ei.motionX = (double) (-MathHelper.sin(theta) * magnitude);
								ei.motionZ = (double) (MathHelper.cos(theta) * magnitude);
								ei.motionY = 0.2F;
								drops.add(ei);
								baubles.setStackInSlot(i, ItemStack.EMPTY);
							}
						}
					}
				});
			});
		/*ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = ReflectionHelper.getPrivateValue(EventBus.class, MinecraftForge.EVENT_BUS, "listeners");
		for (Map.Entry<Object, ArrayList<IEventListener>> entry : listeners.entrySet()) {
			if (entry.getKey() instanceof EventHandlerEntity) {
				entry.getValue().stream()
					.filter(h -> h.toString().contains(" playerDeath("));
			}	
		}*/
	}

	public void init() {}

	public void renderWings(ItemStack stack, EntityPlayer player, IRenderBauble.RenderType type, float delta) {}

	public final Flight newFlight(EntityPlayer player) {
		Flight flight = new FlightDefault();
		if (player instanceof EntityPlayerMP) {
			flight.registerFlyingListener(isFlying -> player.capabilities.allowFlying = isFlying);
			flight.registerFlyingListener(isFlying -> {
				if (isFlying) {
					player.dismountRidingEntity();
				}
			});
			flight.registerSyncListener(toPlayers ->
				network.sendToAllWatching(new MessageSyncFlight(player, flight), player, toPlayers.getExclusions(player))
			);
		}
		addFlightListeners(player, flight);
		return flight;
	}

	protected abstract void addFlightListeners(EntityPlayer player, Flight flight);

	public final Network getNetwork() {
		return network;
	}
}
