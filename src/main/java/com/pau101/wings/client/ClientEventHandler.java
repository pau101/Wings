package com.pau101.wings.client;

import com.pau101.wings.WingsMod;
import com.pau101.wings.server.asm.GetCameraEyeHeightEvent;
import com.pau101.wings.server.capability.Flight;
import com.pau101.wings.server.capability.FlightCapability;
import com.pau101.wings.util.Mth;
import net.ilexiconn.llibrary.client.event.ApplyRenderRotationsEvent;
import net.ilexiconn.llibrary.client.event.PlayerModelEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = WingsMod.ID)
public final class ClientEventHandler {
	private ClientEventHandler() {}

	private static final KeyBinding FLY = newKeyBinding("fly", KeyConflictContext.IN_GAME, Keyboard.KEY_R);

	@SubscribeEvent
	public static void onKey(InputEvent.KeyInputEvent event) {
		if (FLY.isPressed()) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			Flight flight = FlightCapability.get(player);
			if (flight.canFly(player)) {
				flight.toggleIsFlying(Flight.PlayerSet.ofOthers());
			}
		}
	}

	@SubscribeEvent
	public static void onSetRotationAngles(PlayerModelEvent.SetRotationAngles event) {
		EntityPlayer player = event.getEntityPlayer();
		float delta = event.getRotation() - player.ticksExisted;
		float amt = FlightCapability.get(player).getFlyingAmount(delta);
		if (amt > 0) {
			ModelBiped model = event.getModel();
			float pitch = event.getRotationPitch();
			model.bipedHead.rotateAngleX = Mth.toRadians(Mth.lerp(pitch, pitch / 4 - 90, amt));
			model.bipedLeftArm.rotateAngleX = Mth.lerp(model.bipedLeftArm.rotateAngleX, -3.2F, amt);
			model.bipedRightArm.rotateAngleX = Mth.lerp(model.bipedRightArm.rotateAngleX, -3.2F, amt);
			model.bipedLeftLeg.rotateAngleX = Mth.lerp(model.bipedLeftLeg.rotateAngleX, 0, amt);
			model.bipedRightLeg.rotateAngleX = Mth.lerp(model.bipedRightLeg.rotateAngleX, 0, amt);
			ModelBase.copyModelAngles(model.bipedHead, model.bipedHeadwear);
			if (model instanceof ModelPlayer) {
				ModelPlayer playerModel = (ModelPlayer) model;
				ModelBase.copyModelAngles(playerModel.bipedLeftLeg, playerModel.bipedLeftLegwear);
				ModelBase.copyModelAngles(playerModel.bipedRightLeg, playerModel.bipedRightLegwear);
				ModelBase.copyModelAngles(playerModel.bipedLeftArm, playerModel.bipedLeftArmwear);
				ModelBase.copyModelAngles(playerModel.bipedRightArm, playerModel.bipedRightArmwear);
			}
		}
	}

	@SubscribeEvent
	public static void onApplyRenderRotations(ApplyRenderRotationsEvent.Post event) {
		EntityLivingBase entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			float delta = event.getPartialTicks();
			float amt = FlightCapability.get(player).getFlyingAmount(delta);
			if (amt > 0) {
				float roll = Mth.lerpDegrees(player.prevRenderYawOffset - player.prevRotationYaw, player.renderYawOffset - player.rotationYaw, delta);
				float pitch = -Mth.lerpDegrees(player.prevRotationPitch, player.rotationPitch, delta) - 90;
				GlStateManager.rotate(Mth.lerpDegrees(0, roll, amt), 0, 0, 1);
				GlStateManager.rotate(Mth.lerpDegrees(0, pitch, amt), 1, 0, 0);
				GlStateManager.translate(0, -1.2F * Mth.easeInOut(amt), 0);
			}
		}
	}

	@SubscribeEvent
	public static void onGetCameraEyeHeight(GetCameraEyeHeightEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			Flight flight = FlightCapability.get(player);
			float amt = flight.getFlyingAmount(event.getDelta());
			if (amt != 0 && amt != 1) {
				float a = GetCameraEyeHeightEvent.getGroundEyeHeight(player);
				float b = GetCameraEyeHeightEvent.getFlightEyeHeight(player);
				event.setValue(a + (b - a) * Mth.easeOutCirc(Mth.easeInOut(amt)));
			}
		}
	}

	private static KeyBinding newKeyBinding(String name, KeyConflictContext keyContext, int keyCode) {
		KeyBinding kb = new KeyBinding("key." + WingsMod.ID + "." + name, keyContext, keyCode, "key.categories." + WingsMod.ID);
		ClientRegistry.registerKeyBinding(kb);
		return kb;
	}
}
