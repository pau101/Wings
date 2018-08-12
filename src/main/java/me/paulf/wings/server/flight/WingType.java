package me.paulf.wings.server.flight;

import net.minecraft.util.ResourceLocation;

public interface WingType {
	WingType ABSENT = new WingType() {
		@Override
		public ResourceLocation getTexture() {
			return new ResourceLocation("missingno");
		}

		@Override
		public Animator getAnimator(Animator animator) {
			return animator;
		}
	};

	ResourceLocation getTexture();

	Animator getAnimator(Animator animator);
}
