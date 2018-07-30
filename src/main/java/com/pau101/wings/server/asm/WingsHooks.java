package com.pau101.wings.server.asm;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
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

	public static void onUpdateBodyRotation(EntityLivingBase living, float movementYaw) {
		living.renderYawOffset += MathHelper.wrapDegrees(movementYaw - living.renderYawOffset) * 0.3F;
		GetLivingHeadLimitEvent ev = GetLivingHeadLimitEvent.create(living);
		MinecraftForge.EVENT_BUS.post(ev);
		float hLimit = ev.getHardLimit();
		float sLimit = ev.getSoftLimit();
		float theta = MathHelper.clamp(
			MathHelper.wrapDegrees(living.rotationYaw - living.renderYawOffset),
			-hLimit,
			hLimit
		);
		living.renderYawOffset = living.rotationYaw - theta;
		if (theta * theta > sLimit * sLimit) {
			living.renderYawOffset += theta * 0.2F;
		}
	}

	public static void onTurn(Entity entity, float deltaYaw) {
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) entity;
			float theta = MathHelper.wrapDegrees(living.rotationYaw - living.renderYawOffset);
			GetLivingHeadLimitEvent ev = GetLivingHeadLimitEvent.create(living);
			MinecraftForge.EVENT_BUS.post(ev);
			float limit = ev.getHardLimit();
			if (theta < -limit || theta > limit) {
				living.renderYawOffset += deltaYaw;
				living.prevRenderYawOffset += deltaYaw;
			}
		}
	}
}
