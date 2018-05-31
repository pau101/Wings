package com.pau101.wings.client;

import baubles.api.render.IRenderBauble;
import com.pau101.wings.Proxy;
import com.pau101.wings.client.renderer.WingsRenderer;
import com.pau101.wings.server.capability.Flight;
import com.pau101.wings.server.item.WingsItems;
import com.pau101.wings.server.net.serverbound.MessageControlFlying;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class ClientProxy extends Proxy {
	private final WingsRenderer wingsRenderer = new WingsRenderer();

	@Override
	public void init() {
		super.init();
		ItemColors colors = Minecraft.getMinecraft().getItemColors();
		colors.registerItemColorHandler((stack, pass) -> pass == 0 ? 0x9B172D : 0xFFFFFF, WingsItems.BAT_BLOOD);
	}

	@Override
	public void renderWings(ItemStack stack, EntityPlayer player, IRenderBauble.RenderType type, float delta) {
		wingsRenderer.render(stack, player, type, delta);
	}

	@Override
	public void addFlightListeners(EntityPlayer player, Flight flight) {
		if (player.isUser()) {
			flight.registerSyncListener(toPlayers -> network.sendToServer(new MessageControlFlying(flight.isFlying())));
		}
	}

	/*
	private EntityPlayer player;

	private static final GameProfile profile = new GameProfile(MathHelper.getRandomUUID(), "foobar");

	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END && player != null) {
			player.rotationYaw = player.prevRotationYaw = player.rotationYawHead = player.prevRotationYawHead = 0;
			player.renderYawOffset = player.prevRenderYawOffset = 0;
			player.prevLimbSwingAmount = player.limbSwingAmount = player.limbSwing = 0;
			player.swingProgress = 0;
			player.prevPosZ = -1;
			//player.posX = 643;
			//player.posY = 62;
			//player.posZ = 889;
			player.prevPosY = 63;
			player.rotationPitch = player.prevRotationPitch = 0;
			FlightCapability.get(player).update(player);
		}
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.world;
		if (world == null || mc.player == null || mc.getRenderManager().renderViewEntity == null) {
			return;
		}
		if (player == null || player.world != world) {
			player = new EntityOtherPlayerMP(world, profile);
			IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
			baubles.setStackInSlot(5, new ItemStack(WingsItems.WINGS, 1, StandardWing.FIRE.getMeta()));
			FlightCapability.get(player).setIsFlying(true);
		}
		GlStateManager.enableFog();
		GlStateManager.color(1, 1, 1);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate(
			-TileEntityRendererDispatcher.staticPlayerX - 643,
			-TileEntityRendererDispatcher.staticPlayerY + 62,
			-TileEntityRendererDispatcher.staticPlayerZ + 889
		);
		mc.getRenderManager().renderEntity(player, 0, 0, 0, 0, mc.getRenderPartialTicks(), false);
		GlStateManager.popMatrix();
		GlStateManager.disableFog();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.color(1, 1, 1);
	}*/
}
