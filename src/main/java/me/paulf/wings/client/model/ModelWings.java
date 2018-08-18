package me.paulf.wings.client.model;

import me.paulf.wings.client.flight.FlightView;
import me.paulf.wings.client.flight.FlightViewCapability;
import me.paulf.wings.util.Mth;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public abstract class ModelWings extends ModelBase {
	public static final ModelWings NONE = new ModelWings() {
		@Override
		public void render(EntityPlayer player, FlightView flight, float delta, float scale) {}
	};

	@Override
	public final void render(Entity entity, float limbSwing, float limbSwingAmount, float delta, float yawHead, float pitch, float scale) {
		if (entity instanceof AbstractClientPlayer) {
			AbstractClientPlayer player = (AbstractClientPlayer) entity;
			render(player, FlightViewCapability.get(player), delta, scale);
		}
	}

	public abstract void render(EntityPlayer player, FlightView flight, float delta, float scale);

	static void setAngles(ModelRenderer left, ModelRenderer right, Vec3d angles) {
		right.rotateAngleX = (left.rotateAngleX = Mth.toRadians((float) angles.x));
		right.rotateAngleY = -(left.rotateAngleY = Mth.toRadians((float) angles.y));
		right.rotateAngleZ = -(left.rotateAngleZ = Mth.toRadians((float) angles.z));
	}
}
