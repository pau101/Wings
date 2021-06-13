package me.paulf.wings.server.net;

import net.minecraft.network.PacketBuffer;

public interface Message {
    void encode(PacketBuffer buf);

    void decode(PacketBuffer buf);
}
