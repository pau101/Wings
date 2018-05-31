package com.pau101.wings.client.model;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public final class Model3DTexture extends ModelBox {
	private final int width;

	private final int height;

	private final float u1;

	private final float v1;

	private final float u2;

	private final float v2;

	public Model3DTexture(ModelRenderer model, int textureOffsetX, int textureOffsetY, float posX, float posY, float posZ, int width, int height) {
		super(model, 0, 0, posX, posY, posZ, 0, 0, 0, 0);
		this.width = width;
		this.height = height;
		u1 = textureOffsetX / model.textureWidth;
		v1 = textureOffsetY / model.textureHeight;
		u2 = (textureOffsetX + width) / model.textureWidth;
		v2 = (textureOffsetY + height) / model.textureHeight;
	}

	public Model3DTexture(ModelRenderer model, int textureOffsetX, int textureOffsetY, int width, int height) {
		this(model, textureOffsetX, textureOffsetY, 0, 0, 0, width, height);
	}

	@Override
	public void render(BufferBuilder buf, float scale) {
		Tessellator tessellator = Tessellator.getInstance();
		float x0 = posX1 * scale;
        float x1 = (posX1 + width) * scale;
        float y0 = posY1 * scale;
        float y1 = (posY1 + height) * scale;
        float z0 = posZ1 * scale;
		float z1 = (posZ1 + 1) * scale;
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		buf.pos(x0, y0, z0).tex(u1, v1).normal(0, 0, -1).endVertex();
		buf.pos(x1, y0, z0).tex(u2, v1).normal(0, 0, -1).endVertex();
		buf.pos(x1, y1, z0).tex(u2, v2).normal(0, 0, -1).endVertex();
		buf.pos(x0, y1, z0).tex(u1, v2).normal(0, 0, -1).endVertex();
		buf.pos(x0, y1, z1).tex(u1, v2).normal(0, 0, 1).endVertex();
		buf.pos(x1, y1, z1).tex(u2, v2).normal(0, 0, 1).endVertex();
		buf.pos(x1, y0, z1).tex(u2, v1).normal(0, 0, 1).endVertex();
		buf.pos(x0, y0, z1).tex(u1, v1).normal(0, 0, 1).endVertex();
		float f5 = 0.5F * (u1 - u2) / width;
		float f6 = 0.5F * (v1 - v2) / height;
		for (int k = 0; k < width; k++) {
			float f7 = x0 + k * scale;
			float f8 = u1 + (u2 - u1) * ((float) k / width) - f5;
			buf.pos(f7, y0, z1).tex(f8, v1).normal(-1, 0, 0).endVertex();
			buf.pos(f7, y0, z0).tex(f8, v1).normal(-1, 0, 0).endVertex();
			buf.pos(f7, y1, z0).tex(f8, v2).normal(-1, 0, 0).endVertex();
			buf.pos(f7, y1, z1).tex(f8, v2).normal(-1, 0, 0).endVertex();
		}
		for (int k = 0; k < width; k++) {
			float f8 = u1 + (u2 - u1) * ((float) k / width) - f5;
			float f9 = x0 + (k + 1) * scale;
			buf.pos(f9, y1, z1).tex(f8, v2).normal(1, 0, 0).endVertex();
			buf.pos(f9, y1, z0).tex(f8, v2).normal(1, 0, 0).endVertex();
			buf.pos(f9, y0, z0).tex(f8, v1).normal(1, 0, 0).endVertex();
			buf.pos(f9, y0, z1).tex(f8, v1).normal(1, 0, 0).endVertex();
		}
		for (int k = 0; k < height; k++) {
			float f8 = v1 + (v2 - v1) * ((float) k / height) - f6;
			float f9 = y0 + (k + 1) * scale;
			buf.pos(x0, f9, z0).tex(u1, f8).normal(0, 1, 0).endVertex();
			buf.pos(x1, f9, z0).tex(u2, f8).normal(0, 1, 0).endVertex();
			buf.pos(x1, f9, z1).tex(u2, f8).normal(0, 1, 0).endVertex();
			buf.pos(x0, f9, z1).tex(u1, f8).normal(0, 1, 0).endVertex();
		}
		for (int k = 0; k < height; k++) {
			float f7 = y0 + k * scale;
			float f8 = v1 + (v2 - v1) * ((float) k / height) - f6;
			buf.pos(x1, f7, z0).tex(u2, f8).normal(0, -1, 0).endVertex();
			buf.pos(x0, f7, z0).tex(u1, f8).normal(0, -1, 0).endVertex();
			buf.pos(x0, f7, z1).tex(u1, f8).normal(0, -1, 0).endVertex();
			buf.pos(x1, f7, z1).tex(u2, f8).normal(0, -1, 0).endVertex();
		}
		tessellator.draw();
	}
}
