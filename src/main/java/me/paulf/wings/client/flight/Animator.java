package me.paulf.wings.client.flight;

public interface Animator {
    void beginLand();

    void beginGlide();

    void beginIdle();

    void beginLift();

    void beginFall();

    void update();
}
