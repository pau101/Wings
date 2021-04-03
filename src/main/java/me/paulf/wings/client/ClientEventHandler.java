package me.paulf.wings.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.paulf.wings.WingsMod;
import me.paulf.wings.client.audio.WingsSound;
import me.paulf.wings.client.flight.FlightViews;
import me.paulf.wings.server.apparatus.FlightApparatuses;
import me.paulf.wings.server.asm.ApplyPlayerRotationsEvent;
import me.paulf.wings.server.asm.EmptyOffHandPresentEvent;
import me.paulf.wings.server.asm.GetCameraEyeHeightEvent;
import me.paulf.wings.server.asm.AnimatePlayerModelEvent;
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
	private ClientEventHandler() {}

	@SubscribeEvent
	public static void onAnimatePlayerModel(final AnimatePlayerModelEvent event) {
		final PlayerEntity player = event.getPlayer();
		Flights.get(player).ifPresent(flight -> {
			final float delta = event.getTicksExisted() - player.ticksExisted;
			final float amt = flight.getFlyingAmount(delta);
			if (amt == 0.0F) return;
			final PlayerModel<?> model = event.getModel();
			final float pitch = event.getPitch();
			model.bipedHead.rotateAngleX = Mth.toRadians(Mth.lerp(pitch, pitch / 4.0F - 90.0F, amt));
			model.bipedLeftArm.rotateAngleX = Mth.lerp(model.bipedLeftArm.rotateAngleX, -3.2F, amt);
			model.bipedRightArm.rotateAngleX = Mth.lerp(model.bipedRightArm.rotateAngleX, -3.2F, amt);
			model.bipedLeftLeg.rotateAngleX = Mth.lerp(model.bipedLeftLeg.rotateAngleX, 0.0F, amt);
			model.bipedRightLeg.rotateAngleX = Mth.lerp(model.bipedRightLeg.rotateAngleX, 0.0F, amt);
			model.bipedHeadwear.copyModelAngles(model.bipedHead);
		});
	}

	@SubscribeEvent
	public static void onApplyRotations(final ApplyPlayerRotationsEvent event) {
		Flights.ifPlayer(event.getEntity(), (player, flight) -> {
			MatrixStack matrixStack = event.getMatrixStack();
			final float delta = event.getDelta();
			final float amt = flight.getFlyingAmount(delta);
			if (amt > 0.0F) {
				final float roll = Mth.lerpDegrees(
					player.prevRenderYawOffset - player.prevRotationYaw,
					player.renderYawOffset - player.rotationYaw,
					delta
				);
				final float pitch = -Mth.lerpDegrees(player.prevRotationPitch, player.rotationPitch, delta) - 90.0F;
				matrixStack.rotate(Vector3f.ZP.rotationDegrees(Mth.lerpDegrees(0.0F, roll, amt)));
				matrixStack.rotate(Vector3f.XP.rotationDegrees(Mth.lerpDegrees(0.0F, pitch, amt)));
				matrixStack.translate(0.0D, -1.2D * Mth.easeInOut(amt), 0.0D);
			}
		});
	}

	@SubscribeEvent
	public static void onGetCameraEyeHeight(final GetCameraEyeHeightEvent event) {
		final Entity entity = event.getEntity();
		if (entity instanceof ClientPlayerEntity) {
			FlightViews.get((ClientPlayerEntity) entity).ifPresent(flight ->
				flight.tickEyeHeight(event.getValue(), event::setValue)
			);
		}
	}

	@SubscribeEvent
	public static void onCameraSetup(final EntityViewRenderEvent.CameraSetup event) {
		Flights.ifPlayer(Minecraft.getInstance().renderViewEntity, (player, flight) -> {
			final float delta = (float) event.getRenderPartialTicks();
			final float amt = flight.getFlyingAmount(delta);
			if (amt > 0.0F) {
				final float roll = Mth.lerpDegrees(
					player.prevRenderYawOffset - player.prevRotationYaw,
					player.renderYawOffset - player.rotationYaw,
					delta
				);
				event.setRoll(Mth.lerpDegrees(0.0F, -roll * 0.25F, amt));
			}
		});
	}

	@SubscribeEvent
	public static void onEmptyOffHandPresentEvent(final EmptyOffHandPresentEvent event) {
		Flights.get(event.getPlayer()).ifPresent(flight -> {
			if (flight.isFlying()) {
			 event.setResult(Event.Result.ALLOW);
			}
		});
	}

	@SubscribeEvent
	public static void onEntityJoinWorld(final EntityJoinWorldEvent event) {
		Flights.ifPlayer(event.getEntity(), PlayerEntity::isUser, (player, flight) ->
			Minecraft.getInstance().getSoundHandler().play(new WingsSound(player, flight))
		);
	}

	@SubscribeEvent
	public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
		final PlayerEntity entity;
		if (event.phase == TickEvent.Phase.END && (entity = event.player) instanceof ClientPlayerEntity) {
			final ClientPlayerEntity player = (ClientPlayerEntity) entity;
			FlightViews.get(player).ifPresent(flight -> flight.tick(FlightApparatuses.find(player)));
		}
	}
}
