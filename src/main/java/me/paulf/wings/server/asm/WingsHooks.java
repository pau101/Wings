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
    private WingsHooks() {
    }

    public static boolean onFlightCheck(LivingEntity living, boolean defaultValue) {
        return living instanceof PlayerEntity && WingsHooks.onFlightCheck((PlayerEntity) living, defaultValue);
    }

    public static boolean onFlightCheck(PlayerEntity player, boolean defaultValue) {
        if (defaultValue) return true;
        PlayerFlightCheckEvent ev = new PlayerFlightCheckEvent(player);
        MinecraftForge.EVENT_BUS.post(ev);
        return ev.isFlying();
    }

    public static float onGetCameraEyeHeight(Entity entity, float eyeHeight) {
        GetCameraEyeHeightEvent ev = GetCameraEyeHeightEvent.create(entity, eyeHeight);
        MinecraftForge.EVENT_BUS.post(ev);
        return ev.getValue();
    }

    public static boolean onUpdateBodyRotation(LivingEntity living, float movementYaw) {
        GetLivingHeadLimitEvent ev = GetLivingHeadLimitEvent.create(living);
        MinecraftForge.EVENT_BUS.post(ev);
        if (ev.isVanilla()) return false;
        living.yBodyRot += MathHelper.wrapDegrees(movementYaw - living.yBodyRot) * 0.3F;
        float hLimit = ev.getHardLimit();
        float sLimit = ev.getSoftLimit();
        float theta = MathHelper.clamp(
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

    public static void onAddFlown(PlayerEntity player, double x, double y, double z) {
        MinecraftForge.EVENT_BUS.post(new PlayerFlownEvent(player, new Vector3d(x, y, z)));
    }

    public static boolean onReplaceItemSlotCheck(Item item, ItemStack stack) {
        return item instanceof ElytraItem || item.getEquipmentSlot(stack) != null;
    }
}
