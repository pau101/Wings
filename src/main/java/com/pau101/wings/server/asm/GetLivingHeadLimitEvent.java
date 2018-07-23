package com.pau101.wings.server.asm;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;

public final class GetLivingHeadLimitEvent extends LivingEvent {
	private float hardLimit;

	private float softLimit;

	private GetLivingHeadLimitEvent(EntityLivingBase living) {
		super(living);
	}

	public void setHardLimit(float hardLimit) {
		this.hardLimit = hardLimit;
	}

	public float getHardLimit() {
		return hardLimit;
	}

	public void setSoftLimit(float softLimit) {
		this.softLimit = softLimit;
	}

	public float getSoftLimit() {
		return softLimit;
	}

	public void disableHardLimit() {
		setHardLimit(Float.POSITIVE_INFINITY);
	}

	public void disableSoftLimit() {
		setSoftLimit(Float.POSITIVE_INFINITY);
	}

	public boolean hasHardLimit() {
		return getHardLimit() < Float.POSITIVE_INFINITY;
	}

	public boolean hasSoftLimit() {
		return getSoftLimit() < Float.POSITIVE_INFINITY;
	}

	public static GetLivingHeadLimitEvent create(EntityLivingBase living) {
		GetLivingHeadLimitEvent ev = new GetLivingHeadLimitEvent(living);
		ev.setHardLimit(75.0F);
		ev.setSoftLimit(50.0F);
		return ev;
	}
}
