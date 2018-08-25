package me.paulf.wings.client.renderer;

import me.paulf.wings.client.flight.FlightView;
import me.paulf.wings.client.flight.FlightViews;
import me.paulf.wings.server.apparatus.FlightApparatuses;
import me.paulf.wings.util.function.FloatConsumer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public final class LayerWings implements LayerRenderer<AbstractClientPlayer> {
	private final RenderPlayer renderer;

	private final TransformFunction transform;

	public LayerWings(RenderPlayer renderer, TransformFunction transform) {
		this.renderer = renderer;
		this.transform = transform;
	}

	@Override
	public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float delta, float age, float yawHead, float headPitch, float scale) {
		ItemStack stack;
		FlightView flight;
		if (!player.isInvisible() && !(stack = FlightApparatuses.find(player)).isEmpty() && (flight = FlightViews.get(player)) != null) {
			flight.ifFormPresent(form -> {
				renderer.bindTexture(form.getTexture());
				GlStateManager.pushMatrix();
				transform.apply(player, scale, renderer.getMainModel().bipedBody::postRender);
				GlStateManager.enableCull();
				form.render(delta, scale);
				if (stack.hasEffect()) {
					LayerArmorBase.renderEnchantedGlint(renderer, player, new ModelBase() {
						@Override
						public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float delta, float yawHead, float pitch, float scale) {
							form.render(delta, scale);
						}
					}, limbSwing, limbSwingAmount, delta, delta, yawHead, headPitch, scale);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				}
				GlStateManager.disableCull();
				GlStateManager.popMatrix();
			});
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

	@FunctionalInterface
	public interface TransformFunction {
		void apply(AbstractClientPlayer player, float scale, FloatConsumer bodyTransform);
	}
}
