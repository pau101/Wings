package me.paulf.wings.server.asm;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public final class GetLivingHeadLimitEvent extends LivingEvent {
    private float hardLimit;

    private float softLimit;

    private GetLivingHeadLimitEvent(LivingEntity living) {
        super(living);
    }

    public void setHardLimit(float hardLimit) {
        this.hardLimit = hardLimit;
    }

    public float getHardLimit() {
        return this.hardLimit;
    }

    public void setSoftLimit(float softLimit) {
        this.softLimit = softLimit;
    }

    public float getSoftLimit() {
        return this.softLimit;
    }

    public void disableHardLimit() {
        this.setHardLimit(Float.POSITIVE_INFINITY);
    }

    public void disableSoftLimit() {
        this.setSoftLimit(Float.POSITIVE_INFINITY);
    }

    public boolean hasHardLimit() {
        return this.getHardLimit() < Float.POSITIVE_INFINITY;
    }

    public boolean hasSoftLimit() {
        return this.getSoftLimit() < Float.POSITIVE_INFINITY;
    }

    public boolean isVanilla() {
        return this.hardLimit == 75.0F && this.softLimit == 50.0F;
    }

    public static GetLivingHeadLimitEvent create(LivingEntity living) {
        GetLivingHeadLimitEvent ev = new GetLivingHeadLimitEvent(living);
        ev.setHardLimit(75.0F);
        ev.setSoftLimit(50.0F);
        return ev;
    }
}
