package com.pau101.wings.server.flight;

public interface WingType {
	WingType ABSENT = new WingType() {
		@Override
		public boolean canFly() {
			return false;
		}

		@Override
		public Animator getAnimator(Animator animator) {
			return animator;
		}
	};

	boolean canFly();

	Animator getAnimator(Animator animator);
}
