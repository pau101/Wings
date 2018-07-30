package me.paulf.wings.server.flight;

import net.minecraft.util.math.Vec3d;

public interface Animator {
	Animator ABSENT = new Animator() {
		@Override
		public void beginFall() {}

		@Override
		public void beginGlide() {}

		@Override
		public void beginIdle() {}

		@Override
		public void beginLift() {}

		@Override
		public Vec3d getWingRotation(int index, float delta) {
			return Vec3d.ZERO;
		}

		@Override
		public Vec3d getFeatherRotation(int index, float delta) {
			return Vec3d.ZERO;
		}

		@Override
		public void update(double dx, double dy, double dz) {}
	};

	void beginFall();

	void beginGlide();

	void beginIdle();

	void beginLift();

	Vec3d getWingRotation(int index, float delta);

	Vec3d getFeatherRotation(int index, float delta);

	void update(double dx, double dy, double dz);
}
