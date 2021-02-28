package me.paulf.wings.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.wings.client.flight.FlightViews;
import me.paulf.wings.server.apparatus.FlightApparatuses;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public final class LayerWings extends LayerRenderer<LivingEntity, BipedModel<LivingEntity>> {
	private final TransformFunction transform;

	public LayerWings(final LivingRenderer<LivingEntity, BipedModel<LivingEntity>> renderer, final TransformFunction transform) {
		super(renderer);
		this.transform = transform;
	}

	@Override
	public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, LivingEntity player, float limbSwing, float limbSwingAmount, float delta, float age, float headYaw, float headPitch) {
		final ItemStack stack;
		if (!player.isInvisible() && !(stack = FlightApparatuses.find(player)).isEmpty()) {
			FlightViews.get(player).ifPresent(flight -> {
				flight.ifFormPresent(form -> {
					// RenderType.getArmorCutoutNoCull(armorResource)
					//buffer.getBuffer(RenderType.getEntityCutout(form.getTexture()));
					IVertexBuilder builder = ItemRenderer.getArmorVertexBuilder(
						buffer,
						RenderType.getEntityCutout(form.getTexture()),
						false,
						stack.hasEffect()
					);
					matrixStack.push();
					this.transform.apply(player, matrixStack);
					form.render(matrixStack, builder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F, delta);
					matrixStack.pop();
				});
			});
		}
	}

	@FunctionalInterface
	public interface TransformFunction {
		void apply(final LivingEntity player, final MatrixStack stack);
	}
}
