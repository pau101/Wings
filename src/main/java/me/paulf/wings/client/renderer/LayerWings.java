package me.paulf.wings.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.wings.client.flight.FlightViews;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;

public final class LayerWings extends LayerRenderer<LivingEntity, BipedModel<LivingEntity>> {
    private final TransformFunction transform;

    public LayerWings(LivingRenderer<LivingEntity, BipedModel<LivingEntity>> renderer, TransformFunction transform) {
        super(renderer);
        this.transform = transform;
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, LivingEntity player, float limbSwing, float limbSwingAmount, float delta, float age, float headYaw, float headPitch) {
        if (!player.isInvisible()) {
            FlightViews.get(player).ifPresent(flight -> {
                flight.ifFormPresent(form -> {
                    IVertexBuilder builder = buffer.getBuffer(RenderType.entityCutout(form.getTexture()));
                    matrixStack.pushPose();
                    this.transform.apply(player, matrixStack);
                    form.render(matrixStack, builder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F, delta);
                    matrixStack.popPose();
                });
            });
        }
    }

    @FunctionalInterface
    public interface TransformFunction {
        void apply(LivingEntity player, MatrixStack stack);
    }
}
