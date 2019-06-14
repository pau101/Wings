package me.paulf.wings.client.renderer;

import me.paulf.wings.client.flight.FlightView;
import me.paulf.wings.client.flight.FlightViews;
import me.paulf.wings.server.apparatus.FlightApparatuses;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public final class LayerWings implements LayerRenderer<EntityLivingBase> {
	private final RenderLivingBase<?> renderer;

	private final TransformFunction transform;

	public LayerWings(final RenderLivingBase<?> renderer, final TransformFunction transform) {
		this.renderer = renderer;
		this.transform = transform;
	}

	@Override
	public void doRenderLayer(final EntityLivingBase player, final float limbSwing, final float limbSwingAmount, final float delta, final float age, final float yawHead, final float headPitch, final float scale) {
		final ItemStack stack;
		final FlightView flight;
		if (!player.isInvisible() && !(stack = FlightApparatuses.find(player)).isEmpty() && (flight = FlightViews.get(player)) != null) {
			flight.ifFormPresent(form -> {
				this.renderer.bindTexture(form.getTexture());
				GlStateManager.pushMatrix();
				this.transform.apply(player, scale);
				GlStateManager.enableCull();
				form.render(delta, scale);
				if (stack.hasEffect()) {
					LayerArmorBase.renderEnchantedGlint(this.renderer, player, new ModelBase() {
						@Override
						public void render(final Entity entityIn, final float limbSwing, final float limbSwingAmount, final float delta, final float yawHead, final float pitch, final float scale) {
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
		void apply(final EntityLivingBase player, final float scale);
	}
}
