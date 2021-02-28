package me.paulf.wings.server.net;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.net.clientbound.MessageSetWingSettings;
import me.paulf.wings.server.net.clientbound.MessageSyncFlight;
import me.paulf.wings.server.net.serverbound.MessageControlFlying;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class Network {
	private final SimpleChannel network = new NetBuilder(new ResourceLocation(WingsMod.ID, "net"))
		.version(1).optionalServer().requiredClient()
		.serverbound(MessageControlFlying::new).consumer(() -> MessageControlFlying::handle)
		.clientbound(MessageSyncFlight::new).consumer(() -> MessageSyncFlight::handle)
		.clientbound(MessageSetWingSettings::new).consumer(() -> MessageSetWingSettings::handle)
		.build();

	public void sendToServer(final Message message) {
		network.sendToServer(message);
	}

	public void sendToPlayer(final Message message, final ServerPlayerEntity player) {
		network.send(PacketDistributor.PLAYER.with(() -> player), message);
	}

	public void sendToAllTracking(final Message message, final Entity entity) {
		network.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
	}
}
