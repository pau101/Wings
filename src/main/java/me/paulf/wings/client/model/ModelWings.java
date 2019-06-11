package me.paulf.wings.client.model;

import me.paulf.wings.client.flight.Animator;
import me.paulf.wings.util.Mth;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

public abstract class ModelWings<A extends Animator> extends ModelBase {
	@Deprecated
	@Override
	public final void render(final Entity entity, final float limbSwing, final float limbSwingAmount, final float age, final float yawHead, final float pitch, final float scale) {}

	@Deprecated
	@Override
	public void setRotationAngles(final float limbSwing, final float limbSwingAmount, final float age, final float yawHead, final float pitch, final float scale, final Entity entity) {}

	@Deprecated
	@Override
	public final void setLivingAnimations(final EntityLivingBase entity, final float limbSwing, final float limbSwingAmount, final float delta) {}

	public abstract void render(A animator, float delta, float scale);

	static void setAngles(final ModelRenderer left, final ModelRenderer right, final Vec3d angles) {
		right.rotateAngleX = (left.rotateAngleX = Mth.toRadians((float) angles.x));
		right.rotateAngleY = -(left.rotateAngleY = Mth.toRadians((float) angles.y));
		right.rotateAngleZ = -(left.rotateAngleZ = Mth.toRadians((float) angles.z));
	}
}
