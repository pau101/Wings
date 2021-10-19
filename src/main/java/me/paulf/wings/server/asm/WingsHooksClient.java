package me.paulf.wings.server.asm;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.paulf.wings.util.Access;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MapItem;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.lang.invoke.MethodHandle;

public final class WingsHooksClient {
    private WingsHooksClient() {
    }

    private static int selectedItemSlot = 0;

    public static void onSetPlayerRotationAngles(LivingEntity living, PlayerModel<?> model, float ageTicks, float pitch) {
        if (living instanceof PlayerEntity) {
            MinecraftForge.EVENT_BUS.post(new AnimatePlayerModelEvent((PlayerEntity) living, model, ageTicks, pitch));
        }
    }

    public static void onApplyPlayerRotations(AbstractClientPlayerEntity player, MatrixStack matrixStack, float delta) {
        MinecraftForge.EVENT_BUS.post(new ApplyPlayerRotationsEvent(player, matrixStack, delta));
    }

    public static void onTurn(Entity entity, float deltaYaw) {
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            float theta = MathHelper.wrapDegrees(living.yRot - living.yBodyRot);
            GetLivingHeadLimitEvent ev = GetLivingHeadLimitEvent.create(living);
            MinecraftForge.EVENT_BUS.post(ev);
            float limit = ev.getHardLimit();
            if (theta < -limit || theta > limit) {
                living.yBodyRot += deltaYaw;
                living.yBodyRotO += deltaYaw;
            }
        }
    }

    public static boolean onCheckRenderEmptyHand(boolean isMainHand, ItemStack itemStackMainHand) {
        return isMainHand || !Holder.OPTIFUCK && !isMap(itemStackMainHand);
    }

    public static boolean onCheckDoReequipAnimation(ItemStack from, ItemStack to, int slot) {
        boolean fromEmpty = from.isEmpty();
        boolean toEmpty = to.isEmpty();
        boolean isOffHand = slot == -1;
        if (toEmpty && isOffHand) {
            Minecraft mc = Minecraft.getInstance();
            ClientPlayerEntity player = mc.player;
            if (player == null) {
                return true;
            }
            boolean fromMap = isMap(GetItemStackMainHand.invoke(mc.getItemInHandRenderer()));
            boolean toMap = isMap(player.getMainHandItem());
            if (fromMap || toMap) {
                return fromMap != toMap;
            }
            if (fromEmpty) {
                EmptyOffHandPresentEvent ev = new EmptyOffHandPresentEvent(player);
                MinecraftForge.EVENT_BUS.post(ev);
                return ev.getResult() != Event.Result.ALLOW;
            }
        }
        if (fromEmpty || toEmpty) {
            return fromEmpty != toEmpty;
        }
        boolean hasSlotChange = !isOffHand && selectedItemSlot != (selectedItemSlot = slot);
        return from.getItem().shouldCauseReequipAnimation(from, to, hasSlotChange);
    }

    private static boolean isMap(ItemStack stack) {
        return stack.getItem() instanceof MapItem;
    }

    private static final class GetItemStackMainHand {
        private GetItemStackMainHand() {
        }

        private static final MethodHandle MH = Access.getter(FirstPersonRenderer.class)
            .name("field_187467_d", "mainHandItem")
            .type(ItemStack.class);

        private static ItemStack invoke(FirstPersonRenderer instance) {
            try {
                return (ItemStack) MH.invokeExact(instance);
            } catch (Throwable t) {
                throw Access.rethrow(t);
            }
        }
    }

    private static final class Holder {
        private static final boolean OPTIFUCK;
        static {
            boolean present;
            try {
                Class.forName("optifine.ZipResourceProvider");
                present = true;
            } catch (ClassNotFoundException thankGod) {
                present = false;
            }
            OPTIFUCK = present;
        }
    }
}
