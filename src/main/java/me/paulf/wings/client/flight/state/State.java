package me.paulf.wings.client.flight.state;

import me.paulf.wings.client.flight.Animator;
import me.paulf.wings.util.Mth;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public abstract class State {
	static final int STATE_DELAY = 2;

	private final int stateDelay;

	private final Consumer<Animator> animation;

	private int time;

	protected State(Consumer<Animator> animation) {
		this(STATE_DELAY, animation);
	}

	protected State(int stateDelay, Consumer<Animator> animation) {
		this.stateDelay = stateDelay;
		this.animation = animation;
	}

	public final State update(boolean isFlying, double velX, double velY, double velZ, EntityPlayer player) {
		if (time++ > stateDelay) {
			return getNext(isFlying, velX, velY, velZ, player);
		}
		return this;
	}

	private State getNext(boolean isFlying, double velX, double velY, double velZ, EntityPlayer player) {
		if (isFlying) {
			if (velY < 0 && player.rotationPitch >= getPitch(velX, velY, velZ)) {
				return getGlide();
			}
			return getLift();
		}
		if (velY < 0) {
			return getFalling(player);
		}
		return getDefault(velY);
	}

	private float getPitch(double velX, double velY, double velZ) {
		return Mth.toDegrees((float) -Math.atan2(velY, MathHelper.sqrt(velX * velX + velZ * velZ)));
	}

	public final void beginAnimation(Animator animator) {
		animation.accept(animator);
	}

	protected State getFall() {
		return new StateFall();
	}

	protected State getLift() {
		return new StateLift();
	}

	protected State getGlide() {
		return new StateGlide();
	}

	protected State getIdle() {
		return new StateIdle();
	}

	protected State getDefault(double velocityY) {
		return getIdle();
	}

	protected State getFalling(EntityPlayer player) {
		return getFall();
	}
}
