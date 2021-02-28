package me.paulf.wings.server.asm;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;

@Event.HasResult
public class PlayerFlightCheckEvent extends PlayerEvent {
	public PlayerFlightCheckEvent(final PlayerEntity player) {
		super(player);
	}
}
