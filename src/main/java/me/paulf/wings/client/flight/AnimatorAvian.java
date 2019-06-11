package me.paulf.wings.client.flight;

import com.google.common.collect.ImmutableMap;
import me.paulf.wings.util.Mth;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.NoiseGeneratorSimplex;

public final class AnimatorAvian implements Animator {
	private static final int LAND_TRANSITION_DURATION = 2;

	private static final int GLIDE_TRANSITION_DURATION = 60;

	private static final int IDLE_TRANSITION_DURATION = 18;

	private static final int LIFT_TRANSITION_DURATION = 20;

	private static final int FALL_TRANSITION_DURATION = 8;

	private final Movement restPosition = new RestPosition();

	private Movement movement = new IdleMovement();

	private float prevFlapCycle;

	private float flapCycle;

	private void beginMovement(final Movement movement, final int transitionDuration) {
		this.setMovement(new Transition(this.movement, movement, transitionDuration));
	}

	private void setMovement(final Movement movement) {
		this.movement.onEnd();
		this.movement = movement;
	}

	private void flap(final float amount) {
		this.flapCycle += amount;
	}

	private float getFlapTime(final float delta) {
		return Mth.lerp(this.prevFlapCycle, this.flapCycle, delta);
	}

	@Override
	public void beginLand() {
		this.beginMovement(new LandMovement(), LAND_TRANSITION_DURATION);
	}

	@Override
	public void beginGlide() {
		this.beginMovement(new GlideMovement(), GLIDE_TRANSITION_DURATION);
	}

	@Override
	public void beginIdle() {
		this.beginMovement(new IdleMovement(), IDLE_TRANSITION_DURATION);
	}

	@Override
	public void beginLift() {
		this.beginMovement(new LiftMovement(), LIFT_TRANSITION_DURATION);
	}

	@Override
	public void beginFall() {
		this.beginMovement(new FallMovement(), FALL_TRANSITION_DURATION);
	}

	public Vec3d getWingRotation(final int index, final float delta) {
		return this.movement.getWingRotation(index, delta);
	}

	public Vec3d getFeatherRotation(final int index, final float delta) {
		return this.movement.getFeatherRotation(index, delta);
	}

	@Override
	public void update() {
		this.prevFlapCycle = this.flapCycle;
		this.flap(this.movement.update());
	}

	private interface Movement {
		Vec3d getWingRotation(int index, float delta);

		Vec3d getFeatherRotation(int index, float delta);

		float update();

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
		public Vec3d getWingRotation(final int index, final float delta) {
			return this.wing.get(index);
		}

		@Override
		public Vec3d getFeatherRotation(final int index, final float delta) {
			return this.feather.get(index);
		}

		@Override
		public float update() {
			return 0.0F;
		}
	}

	private float getWeight(final int index) {
		return Math.min(Math.abs(index - 1), 2) / 2.0F;
	}

	private final class LandMovement implements Movement {
		@Override
		public Vec3d getWingRotation(final int index, final float delta) {
			final float pos = AnimatorAvian.this.getWeight(index + 1);
			final float time = AnimatorAvian.this.getFlapTime(delta);
			final float cycle = time - pos * 1.2F;
			final double x = (Math.sin(cycle + Mth.PI / 2.0D) - 1.0D) / 2.0D * 20.0D + (1.0D - pos) * 50.0D;
			final double y = (Math.sin(cycle) * 20.0D + (1.0D - pos) * 14.0D) *
				(1.0D - pos * (Math.min(Math.sin(cycle + Mth.PI), 0.0D) / 2.0D + 1.0D) * Math.sin(time));
			return AnimatorAvian.this.restPosition.getWingRotation(index, delta).add(x, y, 4.0D);
		}

		@Override
		public Vec3d getFeatherRotation(final int index, final float delta) {
			return AnimatorAvian.this.restPosition.getFeatherRotation(index, delta);
		}

		@Override
		public float update() {
			return 0.67F;
		}
	}

	private final class GlideMovement implements Movement {
		private final NoiseGeneratorSimplex noise = new NoiseGeneratorSimplex();

		private int time;

		@Override
		public Vec3d getWingRotation(final int index, final float delta) {
			final float pos = AnimatorAvian.this.getWeight(index);
			final float time = AnimatorAvian.this.getFlapTime(delta);
			final double y = (Math.sin(time) * 5.0D - 14.0D) * pos;
			return AnimatorAvian.this.restPosition.getWingRotation(index, delta).add(0.0D, y, 0.0D);
		}

		@Override
		public Vec3d getFeatherRotation(final int index, final float delta) {
			final double x = this.noise.getValue((this.time + delta) * 0.17D, index * 0.13D) * 1.25D;
			return AnimatorAvian.this.restPosition.getFeatherRotation(index, delta).add(x, 0.0D, 0.0D);
		}

		@Override
		public float update() {
			this.time++;
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
		public Vec3d getWingRotation(final int index, final float delta) {
			final float pos = AnimatorAvian.this.getWeight(index);
			final float time = AnimatorAvian.this.getFlapTime(delta);
			return this.wing.get(index).add(0.0D, Math.sin(time) * 3.0D * pos, 0.0D);
		}

		@Override
		public Vec3d getFeatherRotation(final int index, final float delta) {
			final float pos = AnimatorAvian.this.getWeight(index);
			final float time = AnimatorAvian.this.getFlapTime(delta);
			return this.feather.get(index).add(0, -Math.sin(time) * 5.0D * pos, 0.0D);
		}

		@Override
		public float update() {
			return 0.035F;
		}
	}

	private final class LiftMovement implements Movement {
		private final int beginDuration = 20 * 5;

		private int beginTime;

		@Override
		public Vec3d getWingRotation(final int index, final float delta) {
			final float pos = AnimatorAvian.this.getWeight(index);
			final float time = AnimatorAvian.this.getFlapTime(delta);
			final float cycle = time - pos * 1.2F;
			final double x = (Math.sin(cycle + Mth.PI / 2.0D) - 1.0D) / 2.0D * 16.0D + 8.0D;
			final double y = (Math.sin(cycle) * 26.0D + 12.0D) *
				(1.0D - pos * (Math.min(Math.sin(cycle + Mth.PI), 0.0D) / 2.0D + 1.0D) * Math.sin(time));
			return AnimatorAvian.this.restPosition.getWingRotation(index, delta).add(x, y, 0.0D);
		}

		@Override
		public Vec3d getFeatherRotation(final int index, final float delta) {
			return AnimatorAvian.this.restPosition.getFeatherRotation(index, delta);
		}

		@Override
		public float update() {
			final float flap = Mth.lerp(0.375F, 0.225F, (float) this.beginTime / this.beginDuration);
			if (this.beginTime < this.beginDuration) {
				this.beginTime++;
			}
			return flap;
		}
	}

	private final class FallMovement implements Movement {
		private final NoiseGeneratorSimplex noise = new NoiseGeneratorSimplex();

		private final WingPose wing = WingPose.builder()
			.with(0, 30.0D, -23.0D, -50.0D)
			.with(1, -10.0D, 5.0D, -10.0D)
			.with(2, -30.0D, -20.0D, -20.0D)
			.with(3, -20.0D, 0.0D, 20.0D)
			.build();

		private int time;

		@Override
		public Vec3d getWingRotation(final int index, final float delta) {
			final double n = this.noise.getValue((this.time + delta) * 0.18D, index * 0.13D) * 0.92D * (index + 1);
			return this.wing.get(index).add(n, 0.0D, n);
		}

		@Override
		public Vec3d getFeatherRotation(final int index, final float delta) {
			final double n = this.noise.getValue((this.time + delta) * 0.2D, index * 0.13D) * 1.75D;
			return new Vec3d(-n, n * 4.0D, 0.0D);
		}

		@Override
		public float update() {
			this.time++;
			return 0.0F;
		}
	}

	private final class Transition implements Movement {
		private final Movement start;

		private final Movement end;

		private final int duration;

		private int lastTime, time;

		private boolean isActive = true;

		private Transition(final Movement start, final Movement end, final int duration) {
			this.start = start;
			this.end = end;
			this.duration = duration;
		}

		@Override
		public Vec3d getWingRotation(final int index, final float delta) {
			return this.lerpRotation(index, delta, Movement::getWingRotation);
		}

		@Override
		public Vec3d getFeatherRotation(final int index, final float delta) {
			return this.lerpRotation(index, delta, Movement::getFeatherRotation);
		}

		private Vec3d lerpRotation(final int index, final float delta, final RotationGetter getter) {
			final Vec3d startRot = getter.get(this.start, index, delta);
			final Vec3d endRot = getter.get(this.end, index, delta);
			final float t = Mth.easeInOut(Mth.lerp(this.lastTime, this.time, delta) / this.duration);
			return new Vec3d(
				Mth.lerpDegrees(startRot.x, endRot.x, t),
				Mth.lerpDegrees(startRot.y, endRot.y, t),
				Mth.lerpDegrees(startRot.z, endRot.z, t)
			);
		}

		@Override
		public float update() {
			this.lastTime = this.time;
			final float flapStart = this.start.update();
			final float flapEnd = this.end.update();
			final float flap = Mth.lerp(flapStart, flapEnd, (float) this.time / this.duration);
			if (this.time < this.duration) {
				this.time++;
			} else if (this.isActive) {
				AnimatorAvian.this.setMovement(this.end);
			}
			return flap;
		}

		@Override
		public void onEnd() {
			this.isActive = false;
		}
	}

	@FunctionalInterface
	private interface RotationGetter {
		Vec3d get(Movement movement, int index, float delta);
	}

	private static final class WingPose {
		private final ImmutableMap<Integer, Vec3d> rotations;

		private WingPose(final ImmutableMap<Integer, Vec3d> rotations) {
			this.rotations = rotations;
		}

		public Vec3d get(final int index) {
			return this.rotations.getOrDefault(index, Vec3d.ZERO);
		}

		private static Builder builder() {
			return new Builder();
		}

		private static final class Builder {
			private final ImmutableMap.Builder<Integer, Vec3d> rotations = ImmutableMap.builder();

			private Builder() {}

			private Builder with(final int index, final double x, final double y, final double z) {
				this.rotations.put(index, new Vec3d(x, y, z));
				return this;
			}

			private WingPose build() {
				return new WingPose(this.rotations.build());
			}
		}
	}
}
