package me.paulf.wings.client.model;

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

	private Model3DTexture(
		final ModelRenderer model,
		final float posX, final float posY, final float posZ,
		final int width, final int height,
		final float u1, final float v1,
		final float u2, final float v2
	) {
		super(model, 0, 0, posX, posY, posZ, 0, 0, 0, 0.0F);
		this.width = width;
		this.height = height;
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
	}

	@Override
	public void render(final BufferBuilder buf, final float scale) {
		final Tessellator tessellator = Tessellator.getInstance();
		final float x0 = this.posX1 * scale;
		final float x1 = (this.posX1 + this.width) * scale;
		final float y0 = this.posY1 * scale;
		final float y1 = (this.posY1 + this.height) * scale;
		final float z0 = this.posZ1 * scale;
		final float z1 = (this.posZ1 + 1) * scale;
		buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
		buf.pos(x1, y0, z0).tex(this.u2, this.v1).normal(0.0F, 0.0F, -1.0F).endVertex();
		buf.pos(x0, y0, z0).tex(this.u1, this.v1).normal(0.0F, 0.0F, -1.0F).endVertex();
		buf.pos(x0, y1, z0).tex(this.u1, this.v2).normal(0.0F, 0.0F, -1.0F).endVertex();
		buf.pos(x1, y1, z0).tex(this.u2, this.v2).normal(0.0F, 0.0F, -1.0F).endVertex();
		buf.pos(x1, y1, z1).tex(this.u2, this.v2).normal(0.0F, 0.0F, 1.0F).endVertex();
		buf.pos(x0, y1, z1).tex(this.u1, this.v2).normal(0.0F, 0.0F, 1.0F).endVertex();
		buf.pos(x0, y0, z1).tex(this.u1, this.v1).normal(0.0F, 0.0F, 1.0F).endVertex();
		buf.pos(x1, y0, z1).tex(this.u2, this.v1).normal(0.0F, 0.0F, 1.0F).endVertex();
		final float f5 = 0.5F * (this.u1 - this.u2) / this.width;
		final float f6 = 0.5F * (this.v1 - this.v2) / this.height;
		for (int k = 0; k < this.width; k++) {
			final float f7 = x0 + k * scale;
			final float f8 = this.u1 + (this.u2 - this.u1) * ((float) k / this.width) - f5;
			buf.pos(f7, y0, z0).tex(f8, this.v1).normal(-1.0F, 0.0F, 0.0F).endVertex();
			buf.pos(f7, y0, z1).tex(f8, this.v1).normal(-1.0F, 0.0F, 0.0F).endVertex();
			buf.pos(f7, y1, z1).tex(f8, this.v2).normal(-1.0F, 0.0F, 0.0F).endVertex();
			buf.pos(f7, y1, z0).tex(f8, this.v2).normal(-1.0F, 0.0F, 0.0F).endVertex();
		}
		for (int k = 0; k < this.width; k++) {
			final float f8 = this.u1 + (this.u2 - this.u1) * ((float) k / this.width) - f5;
			final float f9 = x0 + (k + 1) * scale;
			buf.pos(f9, y1, z0).tex(f8, this.v2).normal(1.0F, 0.0F, 0.0F).endVertex();
			buf.pos(f9, y1, z1).tex(f8, this.v2).normal(1.0F, 0.0F, 0.0F).endVertex();
			buf.pos(f9, y0, z1).tex(f8, this.v1).normal(1.0F, 0.0F, 0.0F).endVertex();
			buf.pos(f9, y0, z0).tex(f8, this.v1).normal(1.0F, 0.0F, 0.0F).endVertex();
		}
		for (int k = 0; k < this.height; k++) {
			final float f8 = this.v1 + (this.v2 - this.v1) * ((float) k / this.height) - f6;
			final float f9 = y0 + (k + 1) * scale;
			buf.pos(x1, f9, z0).tex(this.u2, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
			buf.pos(x0, f9, z0).tex(this.u1, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
			buf.pos(x0, f9, z1).tex(this.u1, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
			buf.pos(x1, f9, z1).tex(this.u2, f8).normal(0.0F, 1.0F, 0.0F).endVertex();
		}
		for (int k = 0; k < this.height; k++) {
			final float f7 = y0 + k * scale;
			final float f8 = this.v1 + (this.v2 - this.v1) * ((float) k / this.height) - f6;
			buf.pos(x0, f7, z0).tex(this.u1, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
			buf.pos(x1, f7, z0).tex(this.u2, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
			buf.pos(x1, f7, z1).tex(this.u2, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
			buf.pos(x0, f7, z1).tex(this.u1, f8).normal(0.0F, -1.0F, 0.0F).endVertex();
		}
		tessellator.draw();
	}

	public static Model3DTexture create(
		final ModelRenderer model,
		final float posX, final float posY, final float posZ,
		final int width, final int height,
		final int u, final int v
	) {
		return new Model3DTexture(
			model,
			posX, posY, posZ,
			width, height,
			u / model.textureWidth, v / model.textureHeight,
			(u + width) / model.textureWidth, (v + height) / model.textureHeight
		);
	}
}
