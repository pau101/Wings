package me.paulf.wings.server.net;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.net.clientbound.MessageSetWingSettings;
import me.paulf.wings.server.net.clientbound.MessageSyncFlight;
import me.paulf.wings.server.net.serverbound.MessageControlFlying;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class Network implements IMessageHandler<Message, IMessage> {
	private final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(WingsMod.ID);

	public Network() {
		this.register(MessageControlFlying.class, 0, Side.SERVER);
		this.register(MessageSyncFlight.class, 1, Side.CLIENT);
		this.register(MessageSetWingSettings.class, 2, Side.CLIENT);
	}

	public void sendToServer(final IMessage message) {
		this.network.sendToServer(message);
	}

	public void sendToPlayer(final IMessage message, final EntityPlayerMP player) {
		this.network.sendTo(message, player);
	}

	public void sendToAllTracking(final IMessage message, final Entity entity) {
		this.network.sendToAllTracking(message, entity);
	}

	@Override
	public IMessage onMessage(final Message message, final MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> message.process(ctx));
		return null;
	}

	public Packet<?> createPacket(final IMessage message) {
		return this.network.getPacketFrom(message);
	}

	private void register(final Class<? extends Message> cls, final int id, final Side side) {
		this.network.registerMessage(this, cls, id, side);
	}
}
