package me.paulf.wings.client.model;

import me.paulf.wings.server.capability.Flight;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

public final class ModelWingsInsectoid extends ModelWings {
	private final ModelRenderer wingLeft;

	private final ModelRenderer wingRight;

	public ModelWingsInsectoid() {
		textureWidth = textureHeight = 64;
		wingLeft = new ModelRenderer(this, 0, 0);
		wingLeft.setRotationPoint(0, 2, 3.5F);
		wingLeft.addBox(0, -8, 0, 19, 24, 0, 0);
		wingRight = new ModelRenderer(this, 0, 24);
		wingRight.setRotationPoint(0, 2, 3.5F);
		wingRight.addBox(-19, -8, 0, 19, 24, 0, 0);
	}

	@Override
	public void render(EntityPlayer player, Flight flight, float delta) {
		setAngles(wingLeft, wingRight, flight.getWingRotation(0, delta));
		GlStateManager.enableCull();
		wingLeft.render(0.0625F);
		wingRight.render(0.0625F);
		GlStateManager.disableCull();
	}
}
