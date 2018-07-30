package com.pau101.wings.client.debug;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.mojang.authlib.GameProfile;
import com.pau101.wings.WingsMod;
import com.pau101.wings.server.capability.FlightCapability;
import com.pau101.wings.server.item.StandardWing;
import com.pau101.wings.server.item.WingsItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;
import java.util.stream.IntStream;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = WingsMod.ID)
public final class DebugFlightAnimation {
	private DebugFlightAnimation() {}

	private static final boolean DEBUG = false;

	private static Object handler = DEBUG ? null : new Object();

	@SubscribeEvent
	public static void init(ModelRegistryEvent event) {
		if (handler == null) {
			MinecraftForge.EVENT_BUS.register(handler = new Handler());
		}
	}

	private static final class Handler {
		private static final GameProfile PROFILE = new GameProfile(
			UUID.fromString("617ab577-0da7-4d6a-a80d-0b516544369d"),
			"ModDeveloper"
		);

		private EntityPlayer player;

		@SubscribeEvent
		public void tick(TickEvent.ClientTickEvent event) {
			if (event.phase == TickEvent.Phase.END) {
				Minecraft mc = Minecraft.getMinecraft();
				World world = mc.world;
				if (world != null && (player == null || player.world != world)) {
					player = new EntityOtherPlayerMP(world, PROFILE) {{
						getDataManager().set(PLAYER_MODEL_FLAG, (byte) 0xFF);
					}};
					player.setPosition(0.0D, 62.0D, 0.0D);
					player.prevPosZ = -1.0D;
					player.prevPosY = 63.0D;
					IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
					ItemStack stack = new ItemStack(WingsItems.WINGS, 1, StandardWing.EVIL.getMeta());
					int slot = IntStream.of(WingsItems.WINGS.getBaubleType(stack).getValidSlots()).findFirst()
						.orElseThrow(AssertionError::new);
					baubles.setStackInSlot(slot, stack);
					FlightCapability.get(player).setIsFlying(true);
				}
				if (player != null && mc.getConnection() != null) {
					player.onUpdate();
				}
			}
		}

		@SubscribeEvent
		public void render(RenderWorldLastEvent event) {
			Minecraft mc = Minecraft.getMinecraft();
			RenderManager manager = mc.getRenderManager();
			if (mc.world != null && mc.player != null && manager.renderViewEntity != null) {
				GlStateManager.enableFog();
				RenderHelper.enableStandardItemLighting();
				GlStateManager.color(1.0F, 1.0F, 1.0F);
				manager.renderEntity(
					player,
					player.posX - TileEntityRendererDispatcher.staticPlayerX,
					player.posY - TileEntityRendererDispatcher.staticPlayerY,
					player.posZ - TileEntityRendererDispatcher.staticPlayerZ,
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
