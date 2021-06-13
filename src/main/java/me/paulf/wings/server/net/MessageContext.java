package me.paulf.wings.server.net;

import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public abstract class MessageContext {
    protected final NetworkEvent.Context context;

    public MessageContext(NetworkEvent.Context context) {
        this.context = context;
    }

    public abstract LogicalSide getSide();
}
