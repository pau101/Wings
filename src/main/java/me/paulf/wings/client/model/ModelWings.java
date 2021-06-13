package me.paulf.wings.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.wings.client.flight.Animator;
import me.paulf.wings.util.Mth;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3d;


public abstract class ModelWings<A extends Animator> extends Model {
    public ModelWings() {
        super(RenderType::entityCutout);
    }

    @Deprecated
    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    }

    public abstract void render(A animator, float delta, MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha);

    static void setAngles(ModelRenderer left, ModelRenderer right, Vector3d angles) {
        right.xRot = (left.xRot = Mth.toRadians((float) angles.x));
        right.yRot = -(left.yRot = Mth.toRadians((float) angles.y));
        right.zRot = -(left.zRot = Mth.toRadians((float) angles.z));
    }
}
