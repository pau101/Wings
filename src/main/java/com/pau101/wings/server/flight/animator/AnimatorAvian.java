package com.pau101.wings.server.flight.animator;

import com.google.common.collect.ImmutableMap;
import com.pau101.wings.server.flight.Animator;
import com.pau101.wings.util.Mth;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.NoiseGeneratorSimplex;

public final class AnimatorAvian implements Animator {
	private static final int FALL_TRANSITION_DURATION = 2;

	private static final int GLIDE_TRANSITION_DURATION = 60;

	private static final int IDLE_TRANSITION_DURATION = 18;

	private static final int LIFT_TRANSITION_DURATION = 20;

	private final Movement restPosition = new RestPosition();

	private Movement movement = new IdleMovement();

	private float prevFlapCycle;

	private float flapCycle;

	private void beginMovement(Movement movement, int transitionDuration) {
		setMovement(new Transition(this.movement, movement, transitionDuration));
	}

	private void setMovement(Movement movement) {
		this.movement.onEnd();
		this.movement = movement;
	}

	private void flap(float amount) {
		flapCycle += amount;
	}

	private float getFlapTime(float delta) {
		return Mth.lerp(prevFlapCycle, flapCycle, delta);
	}

	@Override
	public void beginFall() {
		beginMovement(new FallMovement(), FALL_TRANSITION_DURATION);
	}

	@Override
	public void beginGlide() {
		beginMovement(new GlideMovement(), GLIDE_TRANSITION_DURATION);
	}

	@Override
	public void beginIdle() {
		beginMovement(new IdleMovement(), IDLE_TRANSITION_DURATION);
	}

	@Override
	public void beginLift() {
		beginMovement(new LiftMovement(), LIFT_TRANSITION_DURATION);
	}

	@Override
	public Vec3d getWingRotation(int index, float delta) {
		return movement.getWingRotation(index, delta);
	}

	@Override
	public Vec3d getFeatherRotation(int index, float delta) {
		return movement.getFeatherRotation(index, delta);
	}

	@Override
	public void update(double dx, double dy, double dz) {
		prevFlapCycle = flapCycle;
		flap(movement.update(dx, dy, dz));
	}

	private interface Movement {
		Vec3d getWingRotation(int index, float delta);

		Vec3d getFeatherRotation(int index, float delta);

		float update(double dx, double dy, double dz);

		default void onEnd() {}
	}

	private final class RestPosition implements Movement {
		private final WingPose wing = WingPose.builder()
			.with(0, 0, -23.5F, -16)
			.with(1, 0, 13, 29)
			.with(2, 0, 12, -28)
			.with(3, 0, 4, 18.3F)
			.build();

		private final WingPose feather = WingPose.builder()
			.with(0, 0, 0, 23.48F)
			.build();

		@Override
		public Vec3d getWingRotation(int index, float delta) {
			return wing.get(index);
		}

		@Override
		public Vec3d getFeatherRotation(int index, float delta) {
			return feather.get(index);
		}

		@Override
		public float update(double dx, double dy, double dz) {
			return 0;
		}
	}

	private float getWeight(int index) {
		return Math.min(Math.abs(index - 1), 2) / 2F;
	}

	private final class FallMovement implements Movement {
		@Override
		public Vec3d getWingRotation(int index, float delta) {
			float pos = getWeight(index + 1);
			float time = getFlapTime(delta);
			float cycle = time - pos * 1.2F;
			double x = (Math.sin(cycle + Mth.PI / 2) - 1) / 2 * 20 + (1 - pos) * 50;
			double y = (Math.sin(cycle) * 20 + (1 - pos) * 14) * (1 - pos * (Math.min(Math.sin(cycle + Mth.PI), 0) / 2 + 1) * Math.sin(time));
			return restPosition.getWingRotation(index, delta).addVector(x, y, 4);
		}

		@Override
		public Vec3d getFeatherRotation(int index, float delta) {
			return restPosition.getFeatherRotation(index, delta);
		}

		@Override
		public float update(double dx, double dy, double dz) {
			return 0.67F;
		}
	}

	private final class GlideMovement implements Movement {
		private final NoiseGeneratorSimplex noise = new NoiseGeneratorSimplex();

		private int time;

		@Override
		public Vec3d getWingRotation(int index, float delta) {
			float pos = getWeight(index);
			float time = getFlapTime(delta);
			double y = (Math.sin(time) * 5 - 14) * pos;
			return restPosition.getWingRotation(index, delta).addVector(0, y, 0);
		}

		@Override
		public Vec3d getFeatherRotation(int index, float delta) {
			double x = noise.getValue((time + delta) * 0.17F, index * 0.13F) * 1.25F;
			return restPosition.getFeatherRotation(index, delta).addVector(x, 0, 0);
		}

		@Override
		public float update(double dx, double dy, double dz) {
			time++;
			return 0.045F;
		}
	}

	private final class IdleMovement implements Movement {
		private final WingPose wing = WingPose.builder()
			.with(0, 40, -60, -50)
			.with(1, 72, 10, 100)
			.with(2, 0, -10, -120)
			.with(3, 10, 0, 100)
			.build();

		private final WingPose feather = WingPose.builder()
			.with(0, 10, 20, 23.48F)
			.with(1, 0, 20, -70)
			.with(2, 0, 10, 40)
			.with(3, -20, 0, 20)
			.build();

		@Override
		public Vec3d getWingRotation(int index, float delta) {
			float pos = getWeight(index);
			float time = getFlapTime(delta);
			return wing.get(index).addVector(0, Math.sin(time) * 3 * pos, 0);
		}

		@Override
		public Vec3d getFeatherRotation(int index, float delta) {
			float pos = getWeight(index);
			float time = getFlapTime(delta);
			return feather.get(index).addVector(0, -Math.sin(time) * 5 * pos, 0);
		}

		@Override
		public float update(double dx, double dy, double dz) {
			return 0.035F;
		}
	}

	private final class LiftMovement implements Movement {
		private final int beginDuration = 20 * 5;

		private int beginTime;

		@Override
		public Vec3d getWingRotation(int index, float delta) {
			float pos = getWeight(index);
			float time = getFlapTime(delta);//5.5F
			float cycle = time - pos * 1.2F;
			double x = (Math.sin(cycle + Mth.PI / 2) - 1) / 2 * 16 + 8;
			double y = (Math.sin(cycle) * 26 + 12) * (1 - pos * (Math.min(Math.sin(cycle + Mth.PI), 0) / 2 + 1) * Math.sin(time));
			return restPosition.getWingRotation(index, delta).addVector(x, y, 0);
		}

		@Override
		public Vec3d getFeatherRotation(int index, float delta) {
			return restPosition.getFeatherRotation(index, delta);
		}

		@Override
		public float update(double dx, double dy, double dz) {
			float flap = Mth.lerp(0.375F, 0.225F, (float) beginTime / beginDuration);
			if (beginTime < beginDuration) {
				beginTime++;
			}
			return flap;
		}
	}

	private final class Transition implements Movement {
		private final Movement start;

		private final Movement end;

		private final int duration;

		private int lastTime, time;

		private boolean isActive = true;

		private Transition(Movement start, Movement end, int duration) {
			this.start = start;
			this.end = end;
			this.duration = duration;
		}

		@Override
		public Vec3d getWingRotation(int index, float delta) {
			return lerpRotation(index, delta, Movement::getWingRotation);
		}

		@Override
		public Vec3d getFeatherRotation(int index, float delta) {
			return lerpRotation(index, delta, Movement::getFeatherRotation);
		}

		private Vec3d lerpRotation(int index, float delta, RotationGetter getter) {
			Vec3d startRot = getter.get(start, index, delta);
			Vec3d endRot = getter.get(end, index, delta);
			float t = Mth.easeInOut(Mth.lerp(lastTime, time, delta) / duration);
			return new Vec3d(
				Mth.lerpDegrees(startRot.x, endRot.x, t),
				Mth.lerpDegrees(startRot.y, endRot.y, t),
				Mth.lerpDegrees(startRot.z, endRot.z, t)
			);
		}

		@Override
		public float update(double dx, double dy, double dz) {
			lastTime = time;
			float flapStart = start.update(dx, dy, dz);
			float flapEnd = end.update(dx, dy, dz);
			float flap = Mth.lerp(flapStart, flapEnd, (float) time / duration);
			if (time < duration) {
				time++;
			} else if (isActive) {
				setMovement(end);
			}
			return flap;
		}

		@Override
		public void onEnd() {
			isActive = false;
		}
	}

	@FunctionalInterface
	private interface RotationGetter {
		Vec3d get(Movement movement, int index, float delta);
	}

	private static final class WingPose {
		private final ImmutableMap<Integer, Vec3d> rotations;

		private WingPose(ImmutableMap<Integer, Vec3d> rotations) {
			this.rotations = rotations;
		}

		public Vec3d get(int index) {
			return rotations.getOrDefault(index, Vec3d.ZERO);
		}

		public static Builder builder() {
			return new Builder();
		}

		public static final class Builder {
			private final ImmutableMap.Builder<Integer, Vec3d> rotations = ImmutableMap.builder();

			private Builder() {}

			public Builder with(int index, double x, double y, double z) {
				rotations.put(index, new Vec3d(x, y, z));
				return this;
			}

			public WingPose build() {
				return new WingPose(rotations.build());
			}
		}
	}
}
