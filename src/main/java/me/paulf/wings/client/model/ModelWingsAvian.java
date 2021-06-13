package me.paulf.wings.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.paulf.wings.client.flight.AnimatorAvian;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Objects;

public final class ModelWingsAvian extends ModelWings<AnimatorAvian> {
    private final ModelRenderer root;

    private final ImmutableList<ModelRenderer> bonesLeft, bonesRight;

    private final ImmutableList<ModelRenderer> feathersLeft, feathersRight;

    public ModelWingsAvian() {
        this.texWidth = this.texHeight = 64;
        this.root = new ModelRenderer(this, 0, 0);
        ModelRenderer coracoidLeft = new ModelRenderer(this, 0, 28);
        coracoidLeft.setPos(1.5F, 5.5F, 2.5F);
        coracoidLeft.addBox(0, -1.5F, -1.5F, 5, 3, 3, 0);
        ModelRenderer coracoidRight = new ModelRenderer(this, 0, 34);
        coracoidRight.setPos(-1.5F, 5.5F, 2.5F);
        coracoidRight.addBox(-5, -1.5F, -1.5F, 5, 3, 3, 0);
        ModelRenderer humerusLeft = new ModelRenderer(this, 0, 0);
        humerusLeft.setPos(4.7F, -0.6F, 0.1F);
        humerusLeft.addBox(-0.1F, -1.1F, -2, 7, 3, 4, 0);
        ModelRenderer humerusRight = new ModelRenderer(this, 0, 7);
        humerusRight.setPos(-4.7F, -0.6F, 0.1F);
        humerusRight.addBox(-6.9F, -1.1F, -2, 7, 3, 4, 0);
        ModelRenderer ulnaLeft = new ModelRenderer(this, 22, 0);
        ulnaLeft.setPos(6.5F, 0.2F, 0.1F);
        ulnaLeft.addBox(0, -1.5F, -1.5F, 9, 3, 3, 0);
        ModelRenderer ulnaRight = new ModelRenderer(this, 22, 6);
        ulnaRight.setPos(-6.5F, 0.2F, 0.1F);
        ulnaRight.addBox(-9, -1.5F, -1.5F, 9, 3, 3, 0);
        ModelRenderer carpalsLeft = new ModelRenderer(this, 46, 0);
        carpalsLeft.setPos(8.5F, 0, 0);
        carpalsLeft.addBox(0, -1, -1, 5, 2, 2, 0);
        ModelRenderer carpalsRight = new ModelRenderer(this, 46, 4);
        carpalsRight.setPos(-8.5F, 0, 0);
        carpalsRight.addBox(-5, -1, -1, 5, 2, 2, 0);
        ModelRenderer feathersCoracoidLeft = new ModelRenderer(this);
        feathersCoracoidLeft.setPos(0.4F, 0, 1);
        add3DTexture(feathersCoracoidLeft, 6, 40, 0, 0, -1, 6, 8);
        ModelRenderer feathersCoracoidRight = new ModelRenderer(this);
        feathersCoracoidRight.setPos(-0.4F, 0, 1);
        add3DTexture(feathersCoracoidRight, 0, 40, -6, 0, -1, 6, 8);
        ModelRenderer feathersTertiaryLeft = new ModelRenderer(this);
        feathersTertiaryLeft.setPos(0, 1.5F, 1);
        add3DTexture(feathersTertiaryLeft, 10, 14, 0, 0, -0.5F, 10, 14);
        ModelRenderer feathersTertiaryRight = new ModelRenderer(this);
        feathersTertiaryRight.setPos(0, 1.5F, 1);
        add3DTexture(feathersTertiaryRight, 0, 14, -10, 0, -0.5F, 10, 14);
        ModelRenderer feathersSecondaryLeft = new ModelRenderer(this, 22, 12);
        feathersSecondaryLeft.setPos(0, 1, 0);
        add3DTexture(feathersSecondaryLeft, 31, 14, -2, 0, -0.5F, 11, 12);
        ModelRenderer feathersSecondaryRight = new ModelRenderer(this);
        feathersSecondaryRight.setPos(0, 1, 0);
        add3DTexture(feathersSecondaryRight, 20, 14, -9, 0, -0.5F, 11, 12);
        ModelRenderer feathersPrimaryLeft = new ModelRenderer(this);
        feathersPrimaryLeft.setPos(0, 0, 0);
        add3DTexture(feathersPrimaryLeft, 53, 14, 0, -2.1F, -0.5F, 11, 11);
        ModelRenderer feathersPrimaryRight = new ModelRenderer(this);
        feathersPrimaryRight.setPos(0, 0, 0);
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
    public void render(AnimatorAvian animator, float delta, MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        for (int i = 0; i < this.bonesLeft.size(); i++) {
            ModelRenderer left = this.bonesLeft.get(i);
            ModelRenderer right = this.bonesRight.get(i);
            setAngles(left, right, animator.getWingRotation(i, delta));
        }
        for (int i = 0; i < this.feathersLeft.size(); i++) {
            ModelRenderer left = this.feathersLeft.get(i);
            ModelRenderer right = this.feathersRight.get(i);
            setAngles(left, right, animator.getFeatherRotation(i, delta));
        }
        this.root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private static void add3DTexture(
        ModelRenderer model,
        int u, int v,
        float offX, float offY, float offZ,
        int width, int height
    ) {
        ObjectList<ModelRenderer.ModelBox> cubeList = Objects.requireNonNull(ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.class, model, "field_78804_l")); // "cubeList"
        cubeList.add(Model3DTexture.create(offX, offY, offZ, width, height, u, v, 64, 64));
    }
}
