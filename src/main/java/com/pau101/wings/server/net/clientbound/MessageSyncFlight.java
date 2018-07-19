package com.pau101.wings.server.net.clientbound;

import com.pau101.wings.server.capability.Flight;
import com.pau101.wings.server.capability.FlightCapability;
import com.pau101.wings.server.flight.FlightDefault;
import com.pau101.wings.server.net.Message;
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

	public MessageSyncFlight(EntityPlayer player, Flight flight) {
		this(player.getEntityId(), flight);
	}

	private MessageSyncFlight(int playerId, Flight flight) {
		this.playerId = playerId;
		this.flight = flight;
	}

	@Override
	protected void serialize(PacketBuffer buf) {
		buf.writeVarInt(playerId);
		flight.serialize(buf);
	}

	@Override
	protected void deserialize(PacketBuffer buf) {
		playerId = buf.readVarInt();
		flight.deserialize(buf);
	}

	@Override
	protected void process(MessageContext ctx) {
		FlightCapability.ifPlayer(FMLClientHandler.instance().getWorldClient().getEntityByID(playerId),
			(player, flight) -> flight.clone(this.flight, Flight.PlayerSet.empty())
		);
	}
}
