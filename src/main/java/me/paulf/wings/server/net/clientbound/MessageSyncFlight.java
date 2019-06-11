package me.paulf.wings.server.net.clientbound;

import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.Flights;
import me.paulf.wings.server.flight.FlightDefault;
import me.paulf.wings.server.net.Message;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class MessageSyncFlight extends Message {
	private int playerId;

	private final Flight flight;

	public MessageSyncFlight() {
		this(0, new FlightDefault());
	}

	public MessageSyncFlight(final EntityPlayer player, final Flight flight) {
		this(player.getEntityId(), flight);
	}

	private MessageSyncFlight(final int playerId, final Flight flight) {
		this.playerId = playerId;
		this.flight = flight;
	}

	@Override
	protected void serialize(final PacketBuffer buf) {
		buf.writeVarInt(this.playerId);
		this.flight.serialize(buf);
	}

	@Override
	protected void deserialize(final PacketBuffer buf) {
		this.playerId = buf.readVarInt();
		this.flight.deserialize(buf);
	}

	@Override
	protected void process(final MessageContext ctx) {
		Flights.ifPlayer(FMLClientHandler.instance().getWorldClient().getEntityByID(this.playerId),
			(player, flight) -> flight.clone(this.flight)
		);
	}
}
