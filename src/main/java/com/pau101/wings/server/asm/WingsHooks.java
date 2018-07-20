package com.pau101.wings.server.asm;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public final class WingsHooks {
	private WingsHooks() {}

	public static boolean onFlightCheck(EntityPlayer player, boolean defaultValue) {
		PlayerFlightCheckEvent ev = new PlayerFlightCheckEvent(player);
		MinecraftForge.EVENT_BUS.post(ev);
		return ev.getResult() == Event.Result.ALLOW || ev.getResult() == Event.Result.DEFAULT && defaultValue;
	}

	public static float onGetCameraEyeHeight(Entity entity, float delta) {
		GetCameraEyeHeightEvent ev = GetCameraEyeHeightEvent.create(entity, delta);
		MinecraftForge.EVENT_BUS.post(ev);
		return ev.getValue();
	}
}
