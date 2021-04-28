package me.paulf.wings.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.wings.client.flight.AnimatorInsectoid;
import net.minecraft.client.renderer.model.ModelRenderer;

public final class ModelWingsInsectoid extends ModelWings<AnimatorInsectoid> {
	private final ModelRenderer root;

	private final ModelRenderer wingLeft;

	private final ModelRenderer wingRight;

	public ModelWingsInsectoid() {
		this.texWidth = this.texHeight = 64;
		this.root = new ModelRenderer(this, 0, 0);
		this.wingLeft = new ModelRenderer(this, 0, 0);
		this.wingLeft.setPos(0, 2, 3.5F);
		this.wingLeft.addBox(0, -8, 0, 19, 24, 0, 0);
		this.wingRight = new ModelRenderer(this, 0, 24);
		this.wingRight.setPos(0, 2, 3.5F);
		this.wingRight.addBox(-19, -8, 0, 19, 24, 0, 0);
		this.root.addChild(this.wingLeft);
		this.root.addChild(this.wingRight);
	}

	@Override
	public void render(final AnimatorInsectoid animator, final float delta, final MatrixStack matrixStack, final IVertexBuilder buffer, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
		setAngles(this.wingLeft, this.wingRight, animator.getRotation(delta));
		this.root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}
