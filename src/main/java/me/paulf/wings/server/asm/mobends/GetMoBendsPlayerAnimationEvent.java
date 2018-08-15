package me.paulf.wings.server.asm.mobends;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public final class GetMoBendsPlayerAnimationEvent extends Event {
	private final EntityPlayer player;

	private String name;

	private GetMoBendsPlayerAnimationEvent(EntityPlayer player, String name) {
		this.player = player;
		this.name = name;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public void set(String name) {
		this.name = name;
	}

	public String get() {
		return name;
	}

	public static GetMoBendsPlayerAnimationEvent create(EntityPlayer player) {
		return new GetMoBendsPlayerAnimationEvent(player, "");
	}
}
