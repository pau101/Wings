package me.paulf.wings.server.asm;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;

public final class WingsHooks {
	private WingsHooks() {}

	public static boolean onFlightCheck(final LivingEntity living, final boolean defaultValue) {
		return living instanceof PlayerEntity && WingsHooks.onFlightCheck((PlayerEntity) living, defaultValue);
	}

	public static boolean onFlightCheck(final PlayerEntity player, final boolean defaultValue) {
		if (defaultValue) return true;
		final PlayerFlightCheckEvent ev = new PlayerFlightCheckEvent(player);
		MinecraftForge.EVENT_BUS.post(ev);
		return ev.isFlying();
	}

	public static float onGetCameraEyeHeight(final Entity entity, final float eyeHeight) {
		final GetCameraEyeHeightEvent ev = GetCameraEyeHeightEvent.create(entity, eyeHeight);
		MinecraftForge.EVENT_BUS.post(ev);
		return ev.getValue();
	}

	public static boolean onUpdateBodyRotation(final LivingEntity living, final float movementYaw) {
		final GetLivingHeadLimitEvent ev = GetLivingHeadLimitEvent.create(living);
		MinecraftForge.EVENT_BUS.post(ev);
		if (ev.isVanilla()) return false;
		living.yBodyRot += MathHelper.wrapDegrees(movementYaw - living.yBodyRot) * 0.3F;
		final float hLimit = ev.getHardLimit();
		final float sLimit = ev.getSoftLimit();
		final float theta = MathHelper.clamp(
			MathHelper.wrapDegrees(living.yRot - living.yBodyRot),
			-hLimit,
			hLimit
		);
		living.yBodyRot = living.yRot - theta;
		if (theta * theta > sLimit * sLimit) {
			living.yBodyRot += theta * 0.2F;
		}
		return true;
	}

	public static void onAddFlown(final PlayerEntity player, final double x, final double y, final double z) {
		MinecraftForge.EVENT_BUS.post(new PlayerFlownEvent(player, new Vector3d(x, y, z)));
	}

	public static boolean onReplaceItemSlotCheck(final Item item, final ItemStack stack) {
		return item instanceof ElytraItem || item.getEquipmentSlot(stack) != null;
	}
}
