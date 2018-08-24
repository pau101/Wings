package me.paulf.wings.server.flight.state;

import me.paulf.wings.client.flight.Animator;

public final class StateFall extends State {
	public StateFall() {
		super(Animator::beginFall);
	}

	@Override
	protected State getFall() {
		return this;
	}
}
