package me.paulf.wings.client.flight;

import me.paulf.wings.util.Mth;
import net.minecraft.util.math.vector.Vector3d;

public final class AnimatorInsectoid implements Animator {
    private static final float IDLE_FLAP_RATE = 0.05F;

    private static final float LIFT_FLAP_RATE = 1.2F;

    private float targetFlapRate = IDLE_FLAP_RATE;

    private float flapRate;

    private float prevFlapCycle;

    private float flapCycle;

    @Override
    public void beginLand() {
        this.beginLift();
    }

    @Override
    public void beginGlide() {
        this.beginLift();
    }

    @Override
    public void beginIdle() {
        this.targetFlapRate = IDLE_FLAP_RATE;
    }

    @Override
    public void beginLift() {
        this.targetFlapRate = LIFT_FLAP_RATE;
    }

    @Override
    public void beginFall() {
        this.beginIdle();
    }

    public Vector3d getRotation(float delta) {
        return new Vector3d(0.0D, Math.sin(Mth.lerp(this.prevFlapCycle, this.flapCycle, delta)) * 35.0D - 42.0D, 0.0D);
    }

    @Override
    public void update() {
        this.prevFlapCycle = this.flapCycle;
        this.flapCycle += this.flapRate;
        this.flapRate += (this.targetFlapRate - this.flapRate) * 0.4F;
    }
}
