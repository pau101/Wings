package me.paulf.wings.server.asm;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.eventbus.api.Event;

@Event.HasResult
public final class EmptyOffHandPresentEvent extends Event {
    private final ClientPlayerEntity player;

    public EmptyOffHandPresentEvent(ClientPlayerEntity player) {
        this.player = player;
    }

    public ClientPlayerEntity getPlayer() {
        return this.player;
    }
}
