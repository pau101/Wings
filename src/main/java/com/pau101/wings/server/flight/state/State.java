package com.pau101.wings.server.flight.state;

import com.pau101.wings.server.flight.Animator;
import com.pau101.wings.util.Mth;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public abstract class State {
	protected static final int STATE_DELAY = 2;

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

	public final State update(boolean isFlying, double velocityX, double velocityY, double velocityZ, EntityPlayer player) {
		if (time++ > stateDelay) {
			return getNext(isFlying, velocityX, velocityY, velocityZ, player);
		}
		return this;
	}

	private State getNext(boolean isFlying, double velocityX, double velocityY, double velocityZ, EntityPlayer player) {
		if (isFlying) {
			if (velocityY < 0 && player.rotationPitch >= Mth.toDegrees((float) -Math.atan2(velocityY, MathHelper.sqrt(velocityX * velocityX + velocityZ * velocityZ)))) {
				return getGlide();
			}
			return getLift();
		}
		if (velocityY < 0) {
			return getFalling(player);
		}
		return getDefault(velocityY);
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
