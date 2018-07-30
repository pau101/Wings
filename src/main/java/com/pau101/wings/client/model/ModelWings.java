package com.pau101.wings.client.model;

import com.pau101.wings.server.capability.Flight;
import com.pau101.wings.util.Mth;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public abstract class ModelWings extends ModelBase {
	public static final ModelWings NONE = new ModelWings() {
		@Override
		public void render(ItemStack stack, EntityPlayer player, Flight flight, float delta) {}
	};

	public abstract void render(ItemStack stack, EntityPlayer player, Flight flight, float delta);

	protected static void setAngles(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = Mth.toRadians(x);
		model.rotateAngleY = Mth.toRadians(y);
		model.rotateAngleZ = Mth.toRadians(z);
	}

	protected static void setAngles(ModelRenderer left, ModelRenderer right, Vec3d angles) {
		right.rotateAngleX = (left.rotateAngleX = Mth.toRadians((float) angles.x));
		right.rotateAngleY = -(left.rotateAngleY = Mth.toRadians((float) angles.y));
		right.rotateAngleZ = -(left.rotateAngleZ = Mth.toRadians((float) angles.z));
	}
}
