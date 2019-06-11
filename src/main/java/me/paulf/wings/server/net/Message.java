package me.paulf.wings.server.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class Message implements IMessage {
	@Override
	public final void toBytes(final ByteBuf buf) {
		this.serialize(new PacketBuffer(buf));
	}

	@Override
	public final void fromBytes(final ByteBuf buf) {
		this.deserialize(new PacketBuffer(buf));
	}

	protected abstract void serialize(final PacketBuffer buf);

	protected abstract void deserialize(final PacketBuffer buf);

	protected abstract void process(final MessageContext ctx);
}
