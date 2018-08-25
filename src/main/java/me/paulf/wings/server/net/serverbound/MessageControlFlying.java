package me.paulf.wings.server.net.serverbound;

import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.Flights;
import me.paulf.wings.server.net.Message;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class MessageControlFlying extends Message {
	private boolean isFlying;

	public MessageControlFlying() {}

	public MessageControlFlying(boolean isFlying) {
		this.isFlying = isFlying;
	}

	protected boolean isFlying() {
		return isFlying;
	}

	@Override
	protected void serialize(PacketBuffer buf) {
		buf.writeBoolean(isFlying);
	}

	@Override
	protected void deserialize(PacketBuffer buf) {
		isFlying = buf.readBoolean();
	}

	@Override
	protected void process(MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;
		Flight flight = Flights.get(player);
		if (flight != null && flight.canFly(player)) {
			flight.setIsFlying(isFlying(), Flight.PlayerSet.ofOthers());
		}
	}
}
