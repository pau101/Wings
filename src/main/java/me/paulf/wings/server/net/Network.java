package me.paulf.wings.server.net;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.net.clientbound.MessageSyncFlight;
import me.paulf.wings.server.net.serverbound.MessageControlFlying;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
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
		register(MessageControlFlying.class, 0, Side.SERVER);
		register(MessageSyncFlight.class, 1, Side.CLIENT);
	}

	public void sendToServer(IMessage message) {
		network.sendToServer(message);
	}

	public void sendToPlayer(IMessage message, EntityPlayerMP player) {
		network.sendTo(message, player);
	}

	public void sendToAllTracking(IMessage message, Entity entity) {
		network.sendToAllTracking(message, entity);
	}

	@Override
	public IMessage onMessage(Message message, MessageContext ctx) {
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> message.process(ctx));
		return null;
	}

	private void register(Class<? extends Message> cls, int id, Side side) {
		network.registerMessage(this, cls, id, side);
	}
}
