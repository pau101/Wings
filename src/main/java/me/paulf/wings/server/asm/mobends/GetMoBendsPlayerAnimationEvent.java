package me.paulf.wings.server.asm.mobends;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public final class GetMoBendsPlayerAnimationEvent extends Event {
	private final EntityPlayer player;

	private String name;

	private GetMoBendsPlayerAnimationEvent(final EntityPlayer player, final String name) {
		this.player = player;
		this.name = name;
	}

	public EntityPlayer getPlayer() {
		return this.player;
	}

	public void set(final String name) {
		this.name = name;
	}

	public String get() {
		return this.name;
	}

	public static GetMoBendsPlayerAnimationEvent create(final EntityPlayer player) {
		return new GetMoBendsPlayerAnimationEvent(player, "");
	}
}
