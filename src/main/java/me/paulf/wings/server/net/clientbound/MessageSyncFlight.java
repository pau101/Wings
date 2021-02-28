package me.paulf.wings.server.net.clientbound;

import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.Flights;
import me.paulf.wings.server.flight.FlightDefault;
import me.paulf.wings.server.net.ClientMessageContext;
import me.paulf.wings.server.net.Message;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public final class MessageSyncFlight implements Message {
	private int playerId;

	private final Flight flight;

	public MessageSyncFlight() {
		this(0, new FlightDefault());
	}

	public MessageSyncFlight(final PlayerEntity player, final Flight flight) {
		this(player.getEntityId(), flight);
	}

	private MessageSyncFlight(final int playerId, final Flight flight) {
		this.playerId = playerId;
		this.flight = flight;
	}

	@Override
	public void encode(final PacketBuffer buf) {
		buf.writeVarInt(this.playerId);
		this.flight.serialize(buf);
	}

	@Override
	public void decode(final PacketBuffer buf) {
		this.playerId = buf.readVarInt();
		this.flight.deserialize(buf);
	}

	public static void handle(final MessageSyncFlight message, final ClientMessageContext context) {
		Flights.ifPlayer(context.getWorld().getEntityByID(message.playerId),
			(player, flight) -> flight.clone(message.flight)
		);
	}
}
