package com.pau101.wings.server.flight;

public interface AnimatorFactory {
	boolean provides(Animator animator);

	Animator create();
}
