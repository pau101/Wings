package me.paulf.wings.server.asm.mobends;

import me.paulf.wings.server.asm.plugin.ClassPatchers;
import net.gobbob.mobends.client.model.entity.ModelBendsPlayer;
import net.ilexiconn.llibrary.server.asm.InsnPredicate;
import net.ilexiconn.llibrary.server.asm.RuntimePatcher;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public final class WingsMoBendsRuntimePatcher extends RuntimePatcher {
	@Override
	public void onInit() {
		ClassPatchers.patchMethod(this.patchClass(ModelBendsPlayer.class), ModelPlayer.class, "setRotationAngles", 6, float.class, Entity.class, void.class)
			.apply(Patch.AFTER, new InsnPredicate.Method(EntityLivingBase.class, "isElytraFlying", boolean.class), m -> m
				.var(ALOAD, 7)
				.cast(EntityPlayer.class)
				.node(SWAP)
				.method(INVOKESTATIC, WingsMoBendsHooks.class, "onTestPlayerAnimation", EntityPlayer.class, boolean.class, boolean.class)
			)
			.apply(Patch.REPLACE_NODE, new InsnPredicate.Ldc().cst("elytra"), m -> m
				.method(INVOKESTATIC, WingsMoBendsHooks.class, "getPlayerAnimation", String.class)
			);
	}
}
