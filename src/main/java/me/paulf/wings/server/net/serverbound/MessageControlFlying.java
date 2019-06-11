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

	public MessageControlFlying(final boolean isFlying) {
		this.isFlying = isFlying;
	}

	protected boolean isFlying() {
		return this.isFlying;
	}

	@Override
	protected void serialize(final PacketBuffer buf) {
		buf.writeBoolean(this.isFlying);
	}

	@Override
	protected void deserialize(final PacketBuffer buf) {
		this.isFlying = buf.readBoolean();
	}

	@Override
	protected void process(final MessageContext ctx) {
		final EntityPlayer player = ctx.getServerHandler().player;
		final Flight flight = Flights.get(player);
		if (flight != null && flight.canFly(player)) {
			flight.setIsFlying(this.isFlying(), Flight.PlayerSet.ofOthers());
		}
	}
}
