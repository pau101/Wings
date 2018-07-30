package com.pau101.wings.server.flight.animator;

import com.pau101.wings.server.flight.Animator;
import com.pau101.wings.util.Mth;
import net.minecraft.util.math.Vec3d;

public final class AnimatorInsectoid implements Animator {
	private static final float IDLE_FLAP_RATE = 0.05F;

	private static final float LIFT_FLAP_RATE = 1.2F;

	private float targetFlapRate = IDLE_FLAP_RATE;

	private float flapRate;

	private float prevFlapCycle;

	private float flapCycle;

	@Override
	public void beginFall() {
		beginLift();
	}

	@Override
	public void beginGlide() {
		beginLift();
	}

	@Override
	public void beginIdle() {
		targetFlapRate = IDLE_FLAP_RATE;
	}

	@Override
	public void beginLift() {
		targetFlapRate = LIFT_FLAP_RATE;
	}

	@Override
	public Vec3d getWingRotation(int index, float delta) {
		return new Vec3d(0.0D, Math.sin(Mth.lerp(prevFlapCycle, flapCycle, delta)) * 35.0D - 42.0D, 0.0D);
	}

	@Override
	public Vec3d getFeatherRotation(int index, float delta) {
		return Vec3d.ZERO;
	}

	@Override
	public void update(double dx, double dy, double dz) {
		prevFlapCycle = flapCycle;
		flapCycle += flapRate;
		flapRate += (targetFlapRate - flapRate) * 0.4F;
	}
}
