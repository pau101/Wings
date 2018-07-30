package me.paulf.wings.server.asm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

@Event.HasResult
public class PlayerFlightCheckEvent extends PlayerEvent {
	public PlayerFlightCheckEvent(EntityPlayer player) {
		super(player);
	}
}
