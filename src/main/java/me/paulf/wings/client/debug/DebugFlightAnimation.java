package me.paulf.wings.client.debug;

import com.mojang.authlib.GameProfile;
import me.paulf.wings.WingsMod;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.Flights;
import me.paulf.wings.server.item.WingsItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IntHashMap;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = WingsMod.ID)
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

		private EntityPlayer player;

		@SubscribeEvent
		public void tick(final TickEvent.ClientTickEvent event) {
			if (event.phase == TickEvent.Phase.END) {
				final Minecraft mc = Minecraft.getMinecraft();
				final World world = mc.world;
				if (world != null && (this.player == null || this.player.world != world)) {
					this.player = new EntityOtherPlayerMP(world, PROFILE) {{
						this.getDataManager().set(PLAYER_MODEL_FLAG, (byte) 0xFF);
					}};
					this.player.setEntityId(-this.player.getEntityId());
					this.player.setPosition(0.0D, 62.0D, 0.0D);
					this.player.prevPosZ = -1.0D;
					this.player.prevPosY = 63.0D;
					final Item item = WingsItems.ANGEL_WINGS;
					this.player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(item));
					item.onItemRightClick(world, this.player, EnumHand.MAIN_HAND);
					final Flight flight = Flights.get(this.player);
					if (flight != null) {
						flight.setIsFlying(true);
					}
					final IntHashMap<Entity> entities = ReflectionHelper.getPrivateValue(World.class, world, "entitiesById");
					entities.addKey(this.player.getEntityId(), this.player);
				}
				if (this.player != null && mc.getConnection() != null) {
					this.player.ticksExisted++;
					this.player.onUpdate();
				}
			}
		}

		@SubscribeEvent
		public void render(final RenderWorldLastEvent event) {
			final Minecraft mc = Minecraft.getMinecraft();
			final RenderManager manager = mc.getRenderManager();
			if (mc.world != null && mc.player != null && manager.renderViewEntity != null) {
				GlStateManager.enableFog();
				RenderHelper.enableStandardItemLighting();
				GlStateManager.color(1.0F, 1.0F, 1.0F);
				manager.renderEntity(
					this.player,
					this.player.posX - TileEntityRendererDispatcher.staticPlayerX,
					this.player.posY - TileEntityRendererDispatcher.staticPlayerY,
					this.player.posZ - TileEntityRendererDispatcher.staticPlayerZ,
					0.0F,
					event.getPartialTicks(),
					false
				);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.disableFog();
			}
		}
	}
}
