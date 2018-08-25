package me.paulf.wings.server.integration;

import me.paulf.wings.client.renderer.LayerWings;
import me.paulf.wings.server.asm.mobends.GetMoBendsPlayerAnimationEvent;
import me.paulf.wings.server.asm.plugin.Integration;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.Flights;
import me.paulf.wings.util.Mth;
import net.gobbob.mobends.animatedentity.AnimatedEntity;
import net.gobbob.mobends.animation.Animation;
import net.gobbob.mobends.client.event.EventHandlerRenderPlayer;
import net.gobbob.mobends.client.model.ModelRendererBends;
import net.gobbob.mobends.client.model.entity.ModelBendsPlayer;
import net.gobbob.mobends.client.renderer.entity.RenderBendsPlayer;
import net.gobbob.mobends.data.EntityData;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.util.vector.Vector3f;

@Integration(
	id = "mobends_wings",
	name = "Mo' Bending Wings",
	condition = "required-after:wings;after:mobends@[0.24,0.25)"
)
public final class WingsMoBendsIntegration {
	@SidedProxy
	private static Proxy proxy = new Proxy();

	@Mod.EventHandler
	public void init(FMLPreInitializationEvent event) {
		proxy.preinit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	private static class Proxy {
		void preinit() {}

		void init() {}
	}

	public static final class ServerProxy extends Proxy {}

	public static final class ClientProxy extends Proxy {
		@Override
		void preinit() {
			MinecraftForge.EVENT_BUS.register(new Object() {
				@SubscribeEvent
				public void onGetMoBendsPlayerAnimation(GetMoBendsPlayerAnimationEvent event) {
					Flight flight = Flights.get(event.getPlayer());
					if (flight != null && flight.getFlyingAmount(1.0F) > 0.0F) {
						event.set("wings");
					}
				}
			});
		}

		@Override
		void init() {
			for (Object obj : AnimatedEntity.skinMap.values()) {
				RenderBendsPlayer renderer = (RenderBendsPlayer) obj;
				renderer.addLayer(new LayerWings(renderer, (player, scale, bodyTransform) -> {
					bodyTransform.accept(scale);
					GlStateManager.translate(0.0F, -12.0F * scale, 0.0F);
				}));
			}
			AnimatedEntity ae = AnimatedEntity.get("player");
			ae.add(new AnimationWings());
		}
	}

	private static final class AnimationWings extends Animation {
		@Override
		public String getName() {
			return "wings";
		}

		@Override
		public void animate(EntityLivingBase entity, ModelBase model, EntityData data) {
			EntityPlayer player = (EntityPlayer) entity;
			Flight flight = Flights.get(player);
			if (flight != null) {
				animate((ModelBendsPlayer) model, flight);
			}
		}

		private static void animate(ModelBendsPlayer model, Flight flight) {
			bends(model.bipedBody).pre_rotation.setSmooth(new Vector3f(0.0F, 0.0F, 0.0F), 0.5F);
			bends(model.bipedBody).rotation.setSmooth(new Vector3f(0.0F, 0.0F, 0.0F), 0.5F);
			bends(model.bipedRightArm).pre_rotation.setSmooth(new Vector3f(0.0F, 0.0F, 0.0F));
			bends(model.bipedLeftArm).pre_rotation.setSmooth(new Vector3f(0.0F, 0.0F, 0.0F));
			bends(model.bipedRightForeLeg).rotation.setSmoothX(4.0F, 0.1F);
			bends(model.bipedLeftForeLeg).rotation.setSmoothX(4.0F, 0.1F);
			bends(model.bipedRightForeArm).rotation.setSmoothX(-4.0F, 0.1F);
			bends(model.bipedLeftForeArm).rotation.setSmoothX(-4.0F, 0.1F);
			bends(model.bipedHead).pre_rotation.setSmooth(new Vector3f(0.0F, 0.0F, 0.0F));
			bends(model.bipedLeftLeg).rotation.setSmooth(new Vector3f(0.0F, 0.0F, 0.0F), 0.5F);
			bends(model.bipedRightLeg).rotation.setSmooth(new Vector3f(0.0F, 0.0F, 0.0F), 0.5F);
			float amt = flight.getFlyingAmount(EventHandlerRenderPlayer.partialTicks);
			bends(model.bipedHead).rotation.set(Mth.lerp(model.headRotationX, model.headRotationX / 4.0F - 90.0F, amt), model.headRotationY, 0.0F);
			bends(model.bipedLeftArm).rotation.setSmooth(new Vector3f(Mth.toDegrees(-3.2F) * amt, 0.0F, 0.0F), 1.0F);
			bends(model.bipedRightArm).rotation.setSmooth(new Vector3f(Mth.toDegrees(-3.2F) * amt, 0.0F, 0.0F), 1.0F);
			if (!flight.isFlying()) {
				float swing = model.armSwing * 0.6662F;
				float swingAmount = Math.min(1.4F * model.armSwingAmount, 0.15F);
				float var = (swing / Mth.PI) % 2.0F;
				bends(model.bipedRightLeg).rotation.setSmoothX(-8.0F + 1.1F * Mth.toDegrees(MathHelper.cos(swing) * swingAmount),0.2F);
				bends(model.bipedLeftLeg).rotation.setSmoothX(-8.0F + 1.1F * Mth.toDegrees(MathHelper.cos(swing + Mth.PI) * 1.4F * swingAmount),0.2F);
				bends(model.bipedRightLeg).rotation.setSmoothZ(12.0F, 0.2F);
				bends(model.bipedLeftLeg).rotation.setSmoothZ(-12.0F, 0.2F);
				bends(model.bipedLeftForeLeg).rotation.setSmoothX(var > 1.0F ? 65.0F : -8.0F, 0.1F);
				bends(model.bipedRightForeLeg).rotation.setSmoothX(var > 1.0F ? -8.0F : 65.0F, 0.1F);
			}
		}

		private static ModelRendererBends bends(ModelRenderer renderer) {
			return (ModelRendererBends) renderer;
		}
	}
}
