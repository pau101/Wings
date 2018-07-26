package com.pau101.wings.server.asm;

import com.pau101.wings.server.asm.plugin.MethodExt;
import net.ilexiconn.llibrary.server.asm.InsnPredicate;
import net.ilexiconn.llibrary.server.asm.RuntimePatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayer;

public final class WingsRuntimePatcher extends RuntimePatcher {
	@Override
	public void onInit() {
		InsnPredicate.Method isElytraFlying = new MethodExt(EntityLivingBase.class, "isElytraFlying", boolean.class).on(EntityPlayer.class);
		patchClass(EntityPlayer.class)
			.patchMethod("updateSize", void.class)
				.apply(Patch.AFTER, isElytraFlying, m -> m
					.var(ALOAD, 0)
					.node(SWAP)
					.method(INVOKESTATIC, WingsHooks.class, "onFlightCheck", EntityPlayer.class, boolean.class, boolean.class)	
				).pop()
			.patchMethod("getEyeHeight", float.class)
				.apply(Patch.AFTER, isElytraFlying, m -> m
					.var(ALOAD, 0)
					.node(SWAP)
					.method(INVOKESTATIC, WingsHooks.class, "onFlightCheck", EntityPlayer.class, boolean.class, boolean.class)
				);
		patchClass(NetHandlerPlayServer.class)
			.patchMethod("processPlayer", CPacketPlayer.class, void.class)
				.apply(Patch.AFTER, isElytraFlying, m -> m
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
		patchClass(EntityLivingBase.class)
			.patchMethod("updateDistance", float.class, float.class, float.class)
				.apply(Patch.REPLACE, m -> m
					.var(ALOAD, 0)
					.var(FLOAD, 1)
					.method(INVOKESTATIC, WingsHooks.class, "onUpdateBodyRotation", EntityLivingBase.class, float.class, void.class)
					.var(BIPUSH, 0)
					.node(I2F)
					.node(FRETURN)
				).pop()
			.patchMethod("travel", float.class, float.class, float.class, void.class)
				.apply(Patch.REPLACE, m -> m
					.var(ALOAD, 0)
					.var(FLOAD, 1)
					.var(FLOAD, 2)
					.var(FLOAD, 3)
					.method(INVOKESTATIC, WingsHooks.class, "onTravel", EntityLivingBase.class, float.class, float.class, float.class, void.class)
					.node(RETURN)
				).apply(((cls, node) -> {
					// FIXME: temporary until release for https://github.com/gegy1000/LLibraryCore/commit/4519b76
					if (node.tryCatchBlocks != null) {
						node.tryCatchBlocks.clear();
					}
					if (node.localVariables != null) {
						node.localVariables.clear();	
					}
					if (node.visibleLocalVariableAnnotations != null) {
						node.visibleLocalVariableAnnotations.clear();	
					}
					if (node.invisibleLocalVariableAnnotations != null) {
						node.invisibleLocalVariableAnnotations.clear();	
					}
				}));
		patchClass(Entity.class)
			.patchMethod("turn", float.class, float.class, void.class)
				.apply(Patch.BEFORE, data -> data.node.getOpcode() == RETURN, m -> m
					.var(ALOAD, 0)
					.node(DUP)
					.field(GETFIELD, Entity.class, "rotationYaw", float.class)
					.var(FLOAD, 4)
					.node(FSUB)
					.method(INVOKESTATIC, WingsHooks.class, "onTurn", Entity.class, float.class, void.class)
				);
	}
}
