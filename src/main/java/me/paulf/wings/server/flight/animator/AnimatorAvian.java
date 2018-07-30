package me.paulf.wings.server.flight.animator;

import com.google.common.collect.ImmutableMap;
import me.paulf.wings.server.flight.Animator;
import me.paulf.wings.util.Mth;
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
			.with(0, 0.0D, -23.5D, -16.0D)
			.with(1, 0.0D, 13.0D, 29.0D)
			.with(2, 0.0D, 12.0D, -28.0D)
			.with(3, 0.0D, 4.0D, 18.3D)
			.build();

		private final WingPose feather = WingPose.builder()
			.with(0, 0.0D, 0.0D, 23.48D)
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
			return 0.0F;
		}
	}

	private float getWeight(int index) {
		return Math.min(Math.abs(index - 1), 2) / 2.0F;
	}

	private final class FallMovement implements Movement {
		@Override
		public Vec3d getWingRotation(int index, float delta) {
			float pos = getWeight(index + 1);
			float time = getFlapTime(delta);
			float cycle = time - pos * 1.2F;
			double x = (Math.sin(cycle + Mth.PI / 2.0D) - 1.0D) / 2.0D * 20.0D + (1.0D - pos) * 50.0D;
			double y = (Math.sin(cycle) * 20.0D + (1.0D - pos) * 14.0D) *
				(1.0D - pos * (Math.min(Math.sin(cycle + Mth.PI), 0.0D) / 2.0D + 1.0D) * Math.sin(time));
			return restPosition.getWingRotation(index, delta).add(x, y, 4.0D);
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
			double y = (Math.sin(time) * 5.0D - 14.0D) * pos;
			return restPosition.getWingRotation(index, delta).add(0.0D, y, 0.0D);
		}

		@Override
		public Vec3d getFeatherRotation(int index, float delta) {
			double x = noise.getValue((time + delta) * 0.17D, index * 0.13D) * 1.25D;
			return restPosition.getFeatherRotation(index, delta).add(x, 0.0D, 0.0D);
		}

		@Override
		public float update(double dx, double dy, double dz) {
			time++;
			return 0.045F;
		}
	}

	private final class IdleMovement implements Movement {
		private final WingPose wing = WingPose.builder()
			.with(0, 40.0D, -60.0D, -50.0D)
			.with(1, 72.0D, 10.0D, 100.0D)
			.with(2, 0.0D, -10.0D, -120.0D)
			.with(3, 10.0D, 0.0D, 100.0D)
			.build();

		private final WingPose feather = WingPose.builder()
			.with(0, 10.0D, 20.0D, 23.48D)
			.with(1, 0.0D, 20.0D, -70.0D)
			.with(2, 0.0D, 10.0D, 40.0D)
			.with(3, -20.0D, 0.0D, 20.0D)
			.build();

		@Override
		public Vec3d getWingRotation(int index, float delta) {
			float pos = getWeight(index);
			float time = getFlapTime(delta);
			return wing.get(index).add(0.0D, Math.sin(time) * 3.0D * pos, 0.0D);
		}

		@Override
		public Vec3d getFeatherRotation(int index, float delta) {
			float pos = getWeight(index);
			float time = getFlapTime(delta);
			return feather.get(index).add(0, -Math.sin(time) * 5.0D * pos, 0.0D);
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
			float time = getFlapTime(delta);
			float cycle = time - pos * 1.2F;
			double x = (Math.sin(cycle + Mth.PI / 2.0D) - 1.0D) / 2.0D * 16.0D + 8.0D;
			double y = (Math.sin(cycle) * 26.0D + 12.0D) *
				(1.0D - pos * (Math.min(Math.sin(cycle + Mth.PI), 0.0D) / 2.0D + 1.0D) * Math.sin(time));
			return restPosition.getWingRotation(index, delta).add(x, y, 0.0D);
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

		private static Builder builder() {
			return new Builder();
		}

		private static final class Builder {
			private final ImmutableMap.Builder<Integer, Vec3d> rotations = ImmutableMap.builder();

			private Builder() {}

			private Builder with(int index, double x, double y, double z) {
				rotations.put(index, new Vec3d(x, y, z));
				return this;
			}

			private WingPose build() {
				return new WingPose(rotations.build());
			}
		}
	}
}
