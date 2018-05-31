package com.pau101.wings.server.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class Message implements IMessage {
	@Override
	public final void toBytes(ByteBuf buf) {
		serialize(new PacketBuffer(buf));
	}

	@Override
	public final void fromBytes(ByteBuf buf) {
		deserialize(new PacketBuffer(buf));
	}

	protected abstract void serialize(PacketBuffer buf);

	protected abstract void deserialize(PacketBuffer buf);

	protected abstract void process(MessageContext ctx);
}
