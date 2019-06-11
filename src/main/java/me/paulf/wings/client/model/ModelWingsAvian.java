package me.paulf.wings.client.model;

import com.google.common.collect.ImmutableList;
import me.paulf.wings.client.flight.AnimatorAvian;
import net.minecraft.client.model.ModelRenderer;

public final class ModelWingsAvian extends ModelWings<AnimatorAvian> {
	private final ModelRenderer root;

	private final ImmutableList<ModelRenderer> bonesLeft, bonesRight;

	private final ImmutableList<ModelRenderer> feathersLeft, feathersRight;

	public ModelWingsAvian() {
		this.textureWidth = this.textureHeight = 64;
		this.root = new ModelRenderer(this, 0, 0);
		final ModelRenderer coracoidLeft = new ModelRenderer(this, 0, 28);
		coracoidLeft.setRotationPoint(1.5F, 5.5F, 2.5F);
		coracoidLeft.addBox(0, -1.5F, -1.5F, 5, 3, 3, 0);
		final ModelRenderer coracoidRight = new ModelRenderer(this, 0, 34);
		coracoidRight.setRotationPoint(-1.5F, 5.5F, 2.5F);
		coracoidRight.addBox(-5, -1.5F, -1.5F, 5, 3, 3, 0);
		final ModelRenderer humerusLeft = new ModelRenderer(this, 0, 0);
		humerusLeft.setRotationPoint(4.7F, -0.6F, 0.1F);
		humerusLeft.addBox(-0.1F, -1.1F, -2, 7, 3, 4, 0);
		final ModelRenderer humerusRight = new ModelRenderer(this, 0, 7);
		humerusRight.setRotationPoint(-4.7F, -0.6F, 0.1F);
		humerusRight.addBox(-6.9F, -1.1F, -2, 7, 3, 4, 0);
		final ModelRenderer ulnaLeft = new ModelRenderer(this, 22, 0);
		ulnaLeft.setRotationPoint(6.5F, 0.2F, 0.1F);
		ulnaLeft.addBox(0, -1.5F, -1.5F, 9, 3, 3, 0);
		final ModelRenderer ulnaRight = new ModelRenderer(this, 22, 6);
		ulnaRight.setRotationPoint(-6.5F, 0.2F, 0.1F);
		ulnaRight.addBox(-9, -1.5F, -1.5F, 9, 3, 3, 0);
		final ModelRenderer carpalsLeft = new ModelRenderer(this, 46, 0);
		carpalsLeft.setRotationPoint(8.5F, 0, 0);
		carpalsLeft.addBox(0, -1, -1, 5, 2, 2, 0);
		final ModelRenderer carpalsRight = new ModelRenderer(this, 46, 4);
		carpalsRight.setRotationPoint(-8.5F, 0, 0);
		carpalsRight.addBox(-5, -1, -1, 5, 2, 2, 0);
		final ModelRenderer feathersCoracoidLeft = new ModelRenderer(this);
		feathersCoracoidLeft.setRotationPoint(0.4F, 0, 1);
		add3DTexture(feathersCoracoidLeft, 6, 40, 0, 0, -1, 6, 8);
		final ModelRenderer feathersCoracoidRight = new ModelRenderer(this);
		feathersCoracoidRight.setRotationPoint(-0.4F, 0, 1);
		add3DTexture(feathersCoracoidRight, 0, 40, -6, 0, -1, 6, 8);
		final ModelRenderer feathersTertiaryLeft = new ModelRenderer(this);
		feathersTertiaryLeft.setRotationPoint(0, 1.5F, 1);
		add3DTexture(feathersTertiaryLeft, 10, 14, 0, 0, -0.5F, 10, 14);
		final ModelRenderer feathersTertiaryRight = new ModelRenderer(this);
		feathersTertiaryRight.setRotationPoint(0, 1.5F, 1);
		add3DTexture(feathersTertiaryRight, 0, 14, -10, 0, -0.5F, 10, 14);
		final ModelRenderer feathersSecondaryLeft = new ModelRenderer(this, 22, 12);
		feathersSecondaryLeft.setRotationPoint(0, 1, 0);
		add3DTexture(feathersSecondaryLeft, 31, 14, -2, 0, -0.5F, 11, 12);
		final ModelRenderer feathersSecondaryRight = new ModelRenderer(this);
		feathersSecondaryRight.setRotationPoint(0, 1, 0);
		add3DTexture(feathersSecondaryRight, 20, 14, -9, 0, -0.5F, 11, 12);
		final ModelRenderer feathersPrimaryLeft = new ModelRenderer(this);
		feathersPrimaryLeft.setRotationPoint(0, 0, 0);
		add3DTexture(feathersPrimaryLeft, 53, 14, 0, -2.1F, -0.5F, 11, 11);
		final ModelRenderer feathersPrimaryRight = new ModelRenderer(this);
		feathersPrimaryRight.setRotationPoint(0, 0, 0);
		add3DTexture(feathersPrimaryRight, 42, 14, -11, -2.1F, -0.5F, 11, 11);
		ulnaLeft.addChild(carpalsLeft);
		ulnaLeft.addChild(feathersSecondaryLeft);
		ulnaRight.addChild(carpalsRight);
		ulnaRight.addChild(feathersSecondaryRight);
		carpalsLeft.addChild(feathersPrimaryLeft);
		carpalsRight.addChild(feathersPrimaryRight);
		humerusLeft.addChild(ulnaLeft);
		humerusLeft.addChild(feathersTertiaryLeft);
		humerusRight.addChild(ulnaRight);
		humerusRight.addChild(feathersTertiaryRight);
		coracoidLeft.addChild(humerusLeft);
		coracoidLeft.addChild(feathersCoracoidLeft);
		coracoidRight.addChild(humerusRight);
		coracoidRight.addChild(feathersCoracoidRight);
		this.root.addChild(coracoidLeft);
		this.root.addChild(coracoidRight);
		this.bonesLeft = ImmutableList.of(coracoidLeft, humerusLeft, ulnaLeft, carpalsLeft);
		this.bonesRight = ImmutableList.of(coracoidRight, humerusRight, ulnaRight, carpalsRight);
		this.feathersLeft = ImmutableList.of(
			feathersCoracoidLeft, feathersTertiaryLeft,
			feathersSecondaryLeft, feathersPrimaryLeft
		);
		this.feathersRight = ImmutableList.of(
			feathersCoracoidRight, feathersTertiaryRight,
			feathersSecondaryRight, feathersPrimaryRight
		);
	}

	@Override
	public void render(final AnimatorAvian animator, final float delta, final float scale) {
		for (int i = 0; i < this.bonesLeft.size(); i++) {
			final ModelRenderer left = this.bonesLeft.get(i);
			final ModelRenderer right = this.bonesRight.get(i);
			setAngles(left, right, animator.getWingRotation(i, delta));
		}
		for (int i = 0; i < this.feathersLeft.size(); i++) {
			final ModelRenderer left = this.feathersLeft.get(i);
			final ModelRenderer right = this.feathersRight.get(i);
			setAngles(left, right, animator.getFeatherRotation(i, delta));
		}
		this.root.render(scale);
	}

	private static void add3DTexture(
		final ModelRenderer model,
		final int u, final int v,
		final float offX, final float offY, final float offZ,
		final int width, final int height
	) {
		model.cubeList.add(Model3DTexture.create(model, offX, offY, offZ, width, height, u, v));
	}
}
