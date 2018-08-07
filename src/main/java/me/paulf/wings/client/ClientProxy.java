package me.paulf.wings.client;

import baubles.api.render.IRenderBauble;
import me.paulf.wings.Proxy;
import me.paulf.wings.client.renderer.WingsRenderer;
import me.paulf.wings.server.capability.Flight;
import me.paulf.wings.server.item.StandardWing;
import me.paulf.wings.server.item.WingsItems;
import me.paulf.wings.server.net.serverbound.MessageControlFlying;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.player.EntityPlayer;

public final class ClientProxy extends Proxy {
	private final WingsRenderer wingsRenderer = new WingsRenderer();

	@Override
	public void init() {
		super.init();
		ItemColors colors = Minecraft.getMinecraft().getItemColors();
		colors.registerItemColorHandler((stack, pass) -> pass == 0 ? 0x9B172D : 0xFFFFFF, WingsItems.BAT_BLOOD);
	}

	@Override
	public void renderWings(StandardWing type, EntityPlayer player, IRenderBauble.RenderType renderType, float delta) {
		wingsRenderer.render(type, player, renderType, delta);
	}

	@Override
	public void addFlightListeners(EntityPlayer player, Flight flight) {
		if (player.isUser()) {
			Flight.Notifier notifier = Flight.Notifier.of(
				() -> {},
				p -> {},
				() -> network.sendToServer(new MessageControlFlying(flight.isFlying()))
			);
			flight.registerSyncListener(players -> players.notify(notifier));
		}
	}
}
