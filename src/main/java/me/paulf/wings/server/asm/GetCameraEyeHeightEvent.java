package me.paulf.wings.server.asm;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

public final class GetCameraEyeHeightEvent extends Event {
	private final Entity entity;

	private final float delta;

	private float value;

	private GetCameraEyeHeightEvent(final Entity entity, final float delta) {
		this.entity = entity;
		this.delta = delta;
	}

	public Entity getEntity() {
		return this.entity;
	}

	public float getDelta() {
		return this.delta;
	}

	public void setValue(final float value) {
		this.value = value;
	}

	public float getValue() {
		return this.value;
	}

	public static GetCameraEyeHeightEvent create(final Entity entity, final float delta) {
		final GetCameraEyeHeightEvent ev = new GetCameraEyeHeightEvent(entity, delta);
		ev.setValue(entity.getEyeHeight());
		return ev;
	}
}
