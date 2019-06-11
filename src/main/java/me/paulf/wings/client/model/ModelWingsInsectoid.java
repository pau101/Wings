package me.paulf.wings.client.model;

import me.paulf.wings.client.flight.AnimatorInsectoid;
import net.minecraft.client.model.ModelRenderer;

public final class ModelWingsInsectoid extends ModelWings<AnimatorInsectoid> {
	private final ModelRenderer root;

	private final ModelRenderer wingLeft;

	private final ModelRenderer wingRight;

	public ModelWingsInsectoid() {
		this.textureWidth = this.textureHeight = 64;
		this.root = new ModelRenderer(this, 0, 0);
		this.wingLeft = new ModelRenderer(this, 0, 0);
		this.wingLeft.setRotationPoint(0, 2, 3.5F);
		this.wingLeft.addBox(0, -8, 0, 19, 24, 0, 0);
		this.wingRight = new ModelRenderer(this, 0, 24);
		this.wingRight.setRotationPoint(0, 2, 3.5F);
		this.wingRight.addBox(-19, -8, 0, 19, 24, 0, 0);
		this.root.addChild(this.wingLeft);
		this.root.addChild(this.wingRight);
	}

	@Override
	public void render(final AnimatorInsectoid animator, final float delta, final float scale) {
		setAngles(this.wingLeft, this.wingRight, animator.getRotation(delta));
		this.root.render(scale);
	}
}
