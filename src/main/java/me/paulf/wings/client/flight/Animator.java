package me.paulf.wings.client.flight;

public interface Animator {
	void beginFall();

	void beginGlide();

	void beginIdle();

	void beginLift();

	void update();
}
