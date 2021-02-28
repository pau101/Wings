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
		super(RenderType::getEntityCutout);
	}

	@Deprecated
	@Override
	public void render(final MatrixStack matrixStack, final IVertexBuilder buffer, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
	}

	public abstract void render(A animator, float delta, final MatrixStack matrixStack, final IVertexBuilder buffer, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha);

	static void setAngles(final ModelRenderer left, final ModelRenderer right, final Vector3d angles) {
		right.rotateAngleX = (left.rotateAngleX = Mth.toRadians((float) angles.x));
		right.rotateAngleY = -(left.rotateAngleY = Mth.toRadians((float) angles.y));
		right.rotateAngleZ = -(left.rotateAngleZ = Mth.toRadians((float) angles.z));
	}
}
