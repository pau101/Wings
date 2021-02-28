package me.paulf.wings.client.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

// TODO: replace reflection with mixin
public final class Model3DTexture extends ModelRenderer.ModelBox {
	private final int width;

	private final int height;

	private final float u1;

	private final float v1;

	private final float u2;

	private final float v2;

	private Model3DTexture(
		final float posX, final float posY, final float posZ,
		final int width, final int height,
		final float u1, final float v1,
		final float u2, final float v2
	) {
		super(0, 0, posX, posY, posZ, 0, 0, 0, 0.0F, 0.0F, 0.0F, false, 64, 64);
		this.width = width;
		this.height = height;
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
		int faceCount = 2 + 2 * width + 2 * height;
		final String MODEL_BOX_QUADS = "field_78254_i"; // quads
		Object quadsOld = Objects.requireNonNull(ObfuscationReflectionHelper.getPrivateValue(ModelRenderer.ModelBox.class, this, MODEL_BOX_QUADS));
		Class<?> texturedQuadClass = quadsOld.getClass().getComponentType();
		Object vertexPositionsArray = getPrivateValue(((Object[]) quadsOld)[0], "field_78239_a"); // vertexPositions
		Class<?> positionTextureVertexClass = vertexPositionsArray.getClass().getComponentType();
		Constructor<?> positionTextureVertexCtor;
		try {
			positionTextureVertexCtor = positionTextureVertexClass.getDeclaredConstructor(float.class, float.class, float.class, float.class, float.class);
			positionTextureVertexCtor.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		Constructor<?> texturedQuadCtor;
		try {
			texturedQuadCtor = texturedQuadClass.getDeclaredConstructor(vertexPositionsArray.getClass(), float.class, float.class, float.class, float.class, float.class, float.class, boolean.class, Direction.class);
			texturedQuadCtor.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		Object[] quads = (Object[]) Array.newInstance(texturedQuadClass, faceCount);
		int[] quadIndex = {0};
		final float x0 = this.posX1;
		final float x1 = (this.posX1 + this.width);
		final float y0 = this.posY1;
		final float y1 = (this.posY1 + this.height);
		final float z0 = this.posZ1;
		final float z1 = (this.posZ1 + 1);
		FaceAdder faces = (fx0, fy0, fz0, fx1, fy1, fz1, fu1, fv1, fu2, fv2, normal) -> {
			Object[] vertices = (Object[]) Array.newInstance(positionTextureVertexClass, 4);
			try {
				boolean v = normal.getAxis().isVertical();
				vertices[0] = positionTextureVertexCtor.newInstance(fx1, fy0, fz0, 0.0F, 0.0F);
				vertices[1] = positionTextureVertexCtor.newInstance(fx0, fy0, v ? fz0 : fz1, 0.0F, 0.0F);
				vertices[2] = positionTextureVertexCtor.newInstance(fx0, fy1, fz1, 0.0F, 0.0F);
				vertices[3] = positionTextureVertexCtor.newInstance(fx1, fy1, v ? fz1 : fz0, 0.0F, 0.0F);
				quads[quadIndex[0]++] = texturedQuadCtor.newInstance(vertices, fu1, fv1, fu2, fv2, 64, 64, false, normal);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		};
		faces.add(x0, y0, z0, x1, y1, z0, this.u1, this.v1, this.u2, this.v2, Direction.NORTH);
		faces.add(x0, y1, z1, x1, y0, z1, this.u1, this.v2, this.u2, this.v1, Direction.SOUTH);
		final float f5 = 0.5F * (this.u1 - this.u2) / this.width;
		final float f6 = 0.5F * (this.v1 - this.v2) / this.height;
		for (int k = 0; k < this.width; k++) {
			final float f7 = x0 + k;
			final float f8 = this.u1 + (this.u2 - this.u1) * ((float) k / this.width) - f5;
			faces.add(f7, y0, z0, f7, y1, z1, f8, this.v1, f8, this.v2, Direction.WEST);
		}
		for (int k = 0; k < this.width; k++) {
			final float f8 = this.u1 + (this.u2 - this.u1) * ((float) k / this.width) - f5;
			final float f9 = x0 + (k + 1);
			faces.add(f9, y1, z0, f9, y0, z1, f8, this.v2, f8, this.v1, Direction.EAST);
		}
		for (int k = 0; k < this.height; k++) {
			final float f8 = this.v1 + (this.v2 - this.v1) * ((float) k / this.height) - f6;
			final float f9 = y0 + (k + 1);
			faces.add(x0, f9, z0, x1, f9, z1, this.u1, f8, this.u2, f8, Direction.UP);
		}
		for (int k = 0; k < this.height; k++) {
			final float f7 = y0 + k;
			final float f8 = this.v1 + (this.v2 - this.v1) * ((float) k / this.height) - f6;
			faces.add(x1, f7, z0, x0, f7, z1, this.u2, f8, this.u1, f8, Direction.DOWN);
		}
		ObfuscationReflectionHelper.setPrivateValue(ModelRenderer.ModelBox.class, this, quads, MODEL_BOX_QUADS);
	}

	interface FaceAdder {
		void add(float x, float y, float z, float x2, float y2, float z2, float u1, float v1, float u2, float v2, Direction normal);
	}

	@SuppressWarnings("unchecked")
	private static <E, T> T getPrivateValue(final E object, final String fieldName){
		return Objects.requireNonNull(ObfuscationReflectionHelper.getPrivateValue((Class<E>) object.getClass(), object, fieldName));
	}

	public static Model3DTexture create(
		final float posX, final float posY, final float posZ,
		final int width, final int height,
		final int u, final int v,
		final int textureWidth, final int textureHeight
	) {
		return new Model3DTexture(
			posX, posY, posZ,
			width, height,
			(float) u, (float) v,
			(float) (u + width), (float) (v + height)
		);
	}
}
