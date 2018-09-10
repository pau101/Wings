package me.paulf.wings.server.asm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerEvent;

public final class PlayerFlownEvent extends PlayerEvent {
	private final Vec3d direction;

	public PlayerFlownEvent(EntityPlayer player, Vec3d direction) {
		super(player);
		this.direction = direction;
	}

	public Vec3d getDirection() {
		return direction;
	}
}
