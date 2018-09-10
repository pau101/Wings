package me.paulf.wings.server.asm;

import me.paulf.wings.server.asm.plugin.MethodExt;
import net.ilexiconn.llibrary.server.asm.InsnPredicate;
import net.ilexiconn.llibrary.server.asm.MethodPatcher;
import net.ilexiconn.llibrary.server.asm.RuntimePatcher;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.ForgeHooksClient;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;

import java.util.function.Predicate;

public final class WingsRuntimePatcher extends RuntimePatcher {
	@Override
	public void onInit() {
		InsnPredicate.Method isElytraFlying = new MethodExt(
			EntityLivingBase.class,
			"isElytraFlying",
			boolean.class
		).on(EntityPlayer.class);
		patchClass(EntityPlayer.class)
			.patchMethod("updateSize", void.class)
				.apply(Patch.AFTER, isElytraFlying, m -> m
					.var(ALOAD, 0)
					.node(SWAP)
					.method(
						INVOKESTATIC,
						WingsHooks.class, "onFlightCheck",
						EntityPlayer.class, boolean.class,
						boolean.class
					)
				).pop()
			.patchMethod("getEyeHeight", float.class)
				.apply(Patch.AFTER, isElytraFlying, m -> m
					.var(ALOAD, 0)
					.node(SWAP)
					.method(
						INVOKESTATIC,
						WingsHooks.class, "onFlightCheck",
						EntityPlayer.class, boolean.class,
						boolean.class
					)
				).pop()
			.patchMethod("addMovementStat", double.class, double.class, double.class, void.class)
				.apply(Patch.BEFORE, addMovementStatTarget(), m -> m
					.var(ALOAD, 0)
					.var(DLOAD, 1)
					.var(DLOAD, 3)
					.var(DLOAD, 5)
					.method(INVOKESTATIC, WingsHooks.class, "onAddFlown", EntityPlayer.class, double.class, double.class, double.class, void.class)
				);
		patchClass(NetHandlerPlayServer.class)
			.patchMethod("processPlayer", CPacketPlayer.class, void.class)
				.apply(Patch.AFTER, isElytraFlying, m -> m
					.var(ALOAD, 0)
					.field(GETFIELD, NetHandlerPlayServer.class, "player", EntityPlayerMP.class)
					.node(SWAP)
					.method(
						INVOKESTATIC,
						WingsHooks.class, "onFlightCheck",
						EntityPlayer.class, boolean.class,
						boolean.class
					)
				);
		patchClass(EntityRenderer.class)
			.patchMethod("orientCamera", float.class, void.class)
				.apply(Patch.REPLACE_NODE, new InsnPredicate.Method(Entity.class, "getEyeHeight", float.class), m -> m
					.var(FLOAD, 1)
					.method(
						INVOKESTATIC,
						WingsHooks.class, "onGetCameraEyeHeight",
						Entity.class, float.class,
						float.class
					)
				);
		patchClass(EntityLivingBase.class)
			.patchMethod("updateDistance", float.class, float.class, float.class)
				.apply(Patch.REPLACE, m -> m
					.var(ALOAD, 0)
					.var(FLOAD, 1)
					.method(
						INVOKESTATIC,
						WingsHooks.class, "onUpdateBodyRotation",
						EntityLivingBase.class, float.class,
						void.class
					)
					.var(BIPUSH, 0)
					.node(I2F)
					.node(FRETURN)
				);
		patchClass(Entity.class)
			.patchMethod("turn", float.class, float.class, void.class)
				.apply(Patch.BEFORE, new InsnPredicate.Op().opcode(RETURN), m -> m
					.var(ALOAD, 0)
					.node(DUP)
					.field(GETFIELD, Entity.class, "rotationYaw", float.class)
					.var(FLOAD, 4)
					.node(FSUB)
					.method(
						INVOKESTATIC,
						WingsHooksClient.class,
						"onTurn", Entity.class,
						float.class,
						void.class
					)
				);
		patchClass(ItemRenderer.class)
			.patchMethod("renderItemInFirstPerson", AbstractClientPlayer.class, float.class, float.class, EnumHand.class, float.class, ItemStack.class, float.class, void.class)
				.apply(Patch.AFTER, renderFirstPersonHandTarget(), m -> m
					.var(ALOAD, 0)
					.field(GETFIELD, ItemRenderer.class, "itemStackMainHand", ItemStack.class)
					.method(
						INVOKESTATIC,
						WingsHooksClient.class,"onCheckRenderEmptyHand",
						boolean.class, ItemStack.class,
						boolean.class
					)
				);
		patchClass(ForgeHooksClient.class)
			.patchMethod("shouldCauseReequipAnimation", ItemStack.class, ItemStack.class, int.class, boolean.class)
				.apply(Patch.REPLACE, m -> m
					.var(ALOAD, 0)
					.var(ALOAD, 1)
					.var(ILOAD, 2)
					.method(
						INVOKESTATIC,
						WingsHooksClient.class,"onCheckDoReequipAnimation",
						ItemStack.class, ItemStack.class, int.class,
						boolean.class
					)
					.node(IRETURN)
				);
	}

	private static Predicate<MethodPatcher.PredicateData> renderFirstPersonHandTarget() {
		InsnPredicate iload8 = new InsnPredicate.Var().var(8).opcode(ILOAD); 
		MethodExt isInvisible = new MethodExt(Entity.class, "isInvisible", boolean.class)
			.on(AbstractClientPlayer.class);
		return iload8.and(data -> data.node.getNext() != null &&
			data.node.getNext().getNext() != null &&
			isInvisible.test(data.node.getNext().getNext().getNext())
		);
	}

	private static Predicate<MethodPatcher.PredicateData> addMovementStatTarget() {
		InsnPredicate iload7 = new InsnPredicate.Var().var(7).opcode(ILOAD);
		Predicate<AbstractInsnNode> bipush25 = node -> node instanceof IntInsnNode && ((IntInsnNode) node).operand == 25;
		return iload7.and(data -> data.node.getNext() != null && bipush25.test(data.node.getNext()));
	}
}
