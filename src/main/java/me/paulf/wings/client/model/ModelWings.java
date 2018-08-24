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
	public final void render(Entity entity, float limbSwing, float limbSwingAmount, float age, float yawHead, float pitch, float scale) {}

	@Deprecated
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float age, float yawHead, float pitch, float scale, Entity entity) {}

	@Deprecated
	@Override
	public final void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float delta) {}

	public abstract void render(A animator, float delta, float scale);

	static void setAngles(ModelRenderer left, ModelRenderer right, Vec3d angles) {
		right.rotateAngleX = (left.rotateAngleX = Mth.toRadians((float) angles.x));
		right.rotateAngleY = -(left.rotateAngleY = Mth.toRadians((float) angles.y));
		right.rotateAngleZ = -(left.rotateAngleZ = Mth.toRadians((float) angles.z));
	}
}
