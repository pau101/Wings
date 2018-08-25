package me.paulf.wings.client.flight.state;

import me.paulf.wings.client.flight.Animator;

public final class StateLift extends State {
	public StateLift() {
		this(STATE_DELAY);
	}

	public StateLift(int stateDelay) {
		super(stateDelay, Animator::beginLift);
	}

	@Override
	protected State getLift() {
		return this;
	}

	@Override
	protected State getDefault(double velocityY) {
		return velocityY >= 0 ? getGlide() : super.getDefault(velocityY);
	}
}
