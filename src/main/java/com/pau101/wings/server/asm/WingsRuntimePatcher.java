package com.pau101.wings.server.asm;

import net.ilexiconn.llibrary.server.asm.InsnPredicate;
import net.ilexiconn.llibrary.server.asm.RuntimePatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayer;

public final class WingsRuntimePatcher extends RuntimePatcher {
	@Override
	public void onInit() {
		patchClass(EntityPlayer.class)
			.patchMethod("updateSize", void.class)
				.apply(Patch.AFTER, new InsnPredicate.Method(EntityPlayer.class, "isElytraFlying", boolean.class), m -> m
					.var(ALOAD, 0)
					.node(SWAP)
					.method(INVOKESTATIC, WingsHooks.class, "onFlightCheck", EntityPlayer.class, boolean.class, boolean.class)	
				).pop()
			.patchMethod("getEyeHeight", float.class)
				.apply(Patch.AFTER, new InsnPredicate.Method(EntityPlayer.class, "isElytraFlying", boolean.class), m -> m
					.var(ALOAD, 0)
					.node(SWAP)
					.method(INVOKESTATIC, WingsHooks.class, "onFlightCheck", EntityPlayer.class, boolean.class, boolean.class)
				);
		patchClass(NetHandlerPlayServer.class)
			.patchMethod("processPlayer", CPacketPlayer.class, void.class)
				.apply(Patch.AFTER, new InsnPredicate.Method(EntityPlayer.class, "isElytraFlying", boolean.class), m -> m
					.var(ALOAD, 0)
					.field(GETFIELD, NetHandlerPlayServer.class, "player", EntityPlayerMP.class)
					.node(SWAP)
					.method(INVOKESTATIC, WingsHooks.class, "onFlightCheck", EntityPlayer.class, boolean.class, boolean.class)
				);
		patchClass(EntityRenderer.class)
			.patchMethod("orientCamera", float.class, void.class)
				.apply(Patch.REPLACE_NODE, new InsnPredicate.Method(Entity.class, "getEyeHeight", float.class), m -> m
					.var(FLOAD, 1)
					.method(INVOKESTATIC, WingsHooks.class, "onGetCameraEyeHeight", Entity.class, float.class, float.class)
				);
	}
}
