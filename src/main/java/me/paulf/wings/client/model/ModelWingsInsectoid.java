package me.paulf.wings.client.model;

import me.paulf.wings.server.flight.animator.AnimatorInsectoid;
import net.minecraft.client.model.ModelRenderer;

public final class ModelWingsInsectoid extends ModelWings<AnimatorInsectoid> {
	private final ModelRenderer root;

	private final ModelRenderer wingLeft;

	private final ModelRenderer wingRight;

	public ModelWingsInsectoid() {
		textureWidth = textureHeight = 64;
		root = new ModelRenderer(this, 0, 0);
		wingLeft = new ModelRenderer(this, 0, 0);
		wingLeft.setRotationPoint(0, 2, 3.5F);
		wingLeft.addBox(0, -8, 0, 19, 24, 0, 0);
		wingRight = new ModelRenderer(this, 0, 24);
		wingRight.setRotationPoint(0, 2, 3.5F);
		wingRight.addBox(-19, -8, 0, 19, 24, 0, 0);
		root.addChild(wingLeft);
		root.addChild(wingRight);
	}

	@Override
	public void render(AnimatorInsectoid animator, float delta, float scale) {
		setAngles(wingLeft, wingRight, animator.getRotation(delta));
		root.render(scale);
	}
}
