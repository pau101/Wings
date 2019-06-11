package me.paulf.wings.server.asm;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public final class WingsHooks {
	private WingsHooks() {}

	public static boolean onFlightCheck(final EntityPlayer player, final boolean defaultValue) {
		final PlayerFlightCheckEvent ev = new PlayerFlightCheckEvent(player);
		MinecraftForge.EVENT_BUS.post(ev);
		return ev.getResult() == Event.Result.ALLOW || ev.getResult() == Event.Result.DEFAULT && defaultValue;
	}

	public static float onGetCameraEyeHeight(final Entity entity, final float delta) {
		final GetCameraEyeHeightEvent ev = GetCameraEyeHeightEvent.create(entity, delta);
		MinecraftForge.EVENT_BUS.post(ev);
		return ev.getValue();
	}

	public static void onUpdateBodyRotation(final EntityLivingBase living, final float movementYaw) {
		living.renderYawOffset += MathHelper.wrapDegrees(movementYaw - living.renderYawOffset) * 0.3F;
		final GetLivingHeadLimitEvent ev = GetLivingHeadLimitEvent.create(living);
		MinecraftForge.EVENT_BUS.post(ev);
		final float hLimit = ev.getHardLimit();
		final float sLimit = ev.getSoftLimit();
		final float theta = MathHelper.clamp(
			MathHelper.wrapDegrees(living.rotationYaw - living.renderYawOffset),
			-hLimit,
			hLimit
		);
		living.renderYawOffset = living.rotationYaw - theta;
		if (theta * theta > sLimit * sLimit) {
			living.renderYawOffset += theta * 0.2F;
		}
	}

	public static void onAddFlown(final EntityPlayer player, final double x, final double y, final double z) {
		MinecraftForge.EVENT_BUS.post(new PlayerFlownEvent(player, new Vec3d(x, y, z)));
	}

	public static boolean onReplaceItemSlotCheck(final Item item, final ItemStack stack) {
		return item instanceof ItemElytra || item.getEquipmentSlot(stack) != null;
	}
}
