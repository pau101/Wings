package me.paulf.wings.server.asm;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerFlightCheckEvent extends PlayerEvent {
	private boolean flying;

	public PlayerFlightCheckEvent(final PlayerEntity player) {
		super(player);
	}

	public boolean isFlying() {
		return this.flying;
	}

	public void setFlying() {
		this.flying = true;
	}
}
