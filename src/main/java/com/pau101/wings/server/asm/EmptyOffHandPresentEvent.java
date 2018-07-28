package com.pau101.wings.server.asm;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.Event;

@Event.HasResult
public final class EmptyOffHandPresentEvent extends Event {
	private final EntityPlayerSP player;

	public EmptyOffHandPresentEvent(EntityPlayerSP player) {
		this.player = player;
	}

	public EntityPlayerSP getPlayer() {
		return player;
	}
}
