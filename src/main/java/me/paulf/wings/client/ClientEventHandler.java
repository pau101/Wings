package me.paulf.wings.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.paulf.wings.WingsMod;
import me.paulf.wings.client.audio.WingsSound;
import me.paulf.wings.client.flight.FlightViews;
import me.paulf.wings.server.asm.AnimatePlayerModelEvent;
import me.paulf.wings.server.asm.ApplyPlayerRotationsEvent;
import me.paulf.wings.server.asm.EmptyOffHandPresentEvent;
import me.paulf.wings.server.asm.GetCameraEyeHeightEvent;
import me.paulf.wings.server.flight.Flights;
import me.paulf.wings.util.Mth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = WingsMod.ID)
public final class ClientEventHandler {
    private ClientEventHandler() {
    }

    @SubscribeEvent
    public static void onAnimatePlayerModel(AnimatePlayerModelEvent event) {
        PlayerEntity player = event.getPlayer();
        Flights.get(player).ifPresent(flight -> {
            float delta = event.getTicksExisted() - player.tickCount;
            float amt = flight.getFlyingAmount(delta);
            if (amt == 0.0F) return;
            PlayerModel<?> model = event.getModel();
            float pitch = event.getPitch();
            model.head.xRot = Mth.toRadians(Mth.lerp(pitch, pitch / 4.0F - 90.0F, amt));
            model.leftArm.xRot = Mth.lerp(model.leftArm.xRot, -3.2F, amt);
            model.rightArm.xRot = Mth.lerp(model.rightArm.xRot, -3.2F, amt);
            model.leftLeg.xRot = Mth.lerp(model.leftLeg.xRot, 0.0F, amt);
            model.rightLeg.xRot = Mth.lerp(model.rightLeg.xRot, 0.0F, amt);
            model.hat.copyFrom(model.head);
        });
    }

    @SubscribeEvent
    public static void onApplyRotations(ApplyPlayerRotationsEvent event) {
        Flights.ifPlayer(event.getEntity(), (player, flight) -> {
            MatrixStack matrixStack = event.getMatrixStack();
            float delta = event.getDelta();
            float amt = flight.getFlyingAmount(delta);
            if (amt > 0.0F) {
                float roll = Mth.lerpDegrees(
                    player.yBodyRotO - player.yRotO,
                    player.yBodyRot - player.yRot,
                    delta
                );
                float pitch = -Mth.lerpDegrees(player.xRotO, player.xRot, delta) - 90.0F;
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerpDegrees(0.0F, roll, amt)));
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(Mth.lerpDegrees(0.0F, pitch, amt)));
                matrixStack.translate(0.0D, -1.2D * Mth.easeInOut(amt), 0.0D);
            }
        });
    }

    @SubscribeEvent
    public static void onGetCameraEyeHeight(GetCameraEyeHeightEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ClientPlayerEntity) {
            FlightViews.get((ClientPlayerEntity) entity).ifPresent(flight ->
                flight.tickEyeHeight(event.getValue(), event::setValue)
            );
        }
    }

    @SubscribeEvent
    public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        Flights.ifPlayer(Minecraft.getInstance().cameraEntity, (player, flight) -> {
            float delta = (float) event.getRenderPartialTicks();
            float amt = flight.getFlyingAmount(delta);
            if (amt > 0.0F) {
                float roll = Mth.lerpDegrees(
                    player.yBodyRotO - player.yRotO,
                    player.yBodyRot - player.yRot,
                    delta
                );
                event.setRoll(Mth.lerpDegrees(0.0F, -roll * 0.25F, amt));
            }
        });
    }

    @SubscribeEvent
    public static void onEmptyOffHandPresentEvent(EmptyOffHandPresentEvent event) {
        Flights.get(event.getPlayer()).ifPresent(flight -> {
            if (flight.isFlying()) {
                event.setResult(Event.Result.ALLOW);
            }
        });
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Flights.ifPlayer(event.getEntity(), PlayerEntity::isLocalPlayer, (player, flight) ->
            Minecraft.getInstance().getSoundManager().play(new WingsSound(player, flight))
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity entity = event.player;
        if (event.phase == TickEvent.Phase.END && entity instanceof ClientPlayerEntity) {
            ClientPlayerEntity player = (ClientPlayerEntity) entity;
            FlightViews.get(player).ifPresent(flight -> flight.tick());
        }
    }
}
