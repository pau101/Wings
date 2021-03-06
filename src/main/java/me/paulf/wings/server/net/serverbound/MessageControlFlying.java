package me.paulf.wings.server.net.serverbound;

import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.Flights;
import me.paulf.wings.server.net.Message;
import me.paulf.wings.server.net.ServerMessageContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public final class MessageControlFlying implements Message {
    private boolean isFlying;

    public MessageControlFlying() {
    }

    public MessageControlFlying(boolean isFlying) {
        this.isFlying = isFlying;
    }

    @Override
    public void encode(PacketBuffer buf) {
        buf.writeBoolean(this.isFlying);
    }

    @Override
    public void decode(PacketBuffer buf) {
        this.isFlying = buf.readBoolean();
    }

    public static void handle(MessageControlFlying message, ServerMessageContext context) {
        PlayerEntity player = context.getPlayer();
        Flights.get(player).filter(f -> f.canFly(player))
            .ifPresent(flight -> flight.setIsFlying(message.isFlying, Flight.PlayerSet.ofOthers()));
    }
}
