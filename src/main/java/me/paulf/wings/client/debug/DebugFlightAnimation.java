package me.paulf.wings.client.debug;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.paulf.wings.WingsMod;
import me.paulf.wings.server.flight.Flights;
import me.paulf.wings.server.item.WingsItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.UUID;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = WingsMod.ID)
public final class DebugFlightAnimation {
	private DebugFlightAnimation() {}

	private static State state = new DisabledState();

	@SubscribeEvent
	public static void init(final ModelRegistryEvent event) {
		state = state.init();
	}

	private interface State {
		State init();
	}

	protected static final class DisabledState implements State {
		@Override
		public State init() {
			return this;
		}
	}

	private static final class EnabledState implements State {
		@Override
		public State init() {
			return this;
		}
	}

	private static final class EnableState implements State {
		@Override
		public State init() {
			MinecraftForge.EVENT_BUS.register(new Handler());
			return new EnabledState();
		}
	}


	private static final class Handler {
		private static final GameProfile PROFILE = new GameProfile(
			UUID.fromString("617ab577-0da7-4d6a-a80d-0b516544369d"),
			"ModDeveloper"
		);

		private PlayerEntity player;

		@SubscribeEvent
		public void tick(final TickEvent.ClientTickEvent event) {
			if (event.phase == TickEvent.Phase.END) {
				final Minecraft mc = Minecraft.getInstance();
				final ClientWorld world = mc.world;
				if (world != null && (this.player == null || this.player.world != world)) {
					this.player = new RemoteClientPlayerEntity(world, PROFILE) {{
						this.getDataManager().set(PLAYER_MODEL_FLAG, (byte) 0xFF);
					}};
					this.player.setEntityId(-this.player.getEntityId());
					this.player.setPosition(0.0D, 62.0D, 0.0D);
					this.player.prevPosZ = -1.0D;
					this.player.prevPosY = 63.0D;
					final Item item = WingsItems.ANGEL_WINGS.get();
					this.player.setHeldItem(Hand.MAIN_HAND, new ItemStack(item));
					item.onItemRightClick(world, this.player, Hand.MAIN_HAND);
					Flights.get(this.player).ifPresent(flight -> flight.setIsFlying(true));
					final Int2ObjectMap<Entity> entities = ObfuscationReflectionHelper.getPrivateValue(ClientWorld.class, world, "entitiesById");
					entities.put(this.player.getEntityId(), this.player);
				}
				if (this.player != null && mc.getConnection() != null) {
					this.player.ticksExisted++;
					this.player.tick();
				}
			}
		}

		@SubscribeEvent
		public void render(final RenderWorldLastEvent event) {
			final Minecraft mc = Minecraft.getInstance();
			if (mc.world != null && mc.player != null && mc.renderViewEntity != null) {
				final EntityRendererManager manager = mc.getRenderManager();
				Vector3d projectedView = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
				manager.renderEntityStatic(
					this.player,
					this.player.getPosX() - projectedView.getX(),
					this.player.getPosY() - projectedView.getY(),
					this.player.getPosZ() - projectedView.getZ(),
					0.0F,
					event.getPartialTicks(),
					event.getMatrixStack(),
					mc.getRenderTypeBuffers().getBufferSource(),
					manager.getPackedLight(this.player, event.getPartialTicks())
				);
			}
		}
	}
}
