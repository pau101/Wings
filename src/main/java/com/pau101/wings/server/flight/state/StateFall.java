package com.pau101.wings.server.flight.state;

import com.pau101.wings.server.flight.Animator;

public final class StateFall extends State {
	public StateFall() {
		super(Animator::beginFall);
	}

	@Override
	protected State getFall() {
		return this;
	}
}
