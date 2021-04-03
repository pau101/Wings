package me.paulf.wings.server.asm;

import net.minecraft.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

public final class GetCameraEyeHeightEvent extends Event {
	private final Entity entity;

	private float value;

	private GetCameraEyeHeightEvent(final Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return this.entity;
	}

	public void setValue(final float value) {
		this.value = value;
	}

	public float getValue() {
		return this.value;
	}

	public static GetCameraEyeHeightEvent create(final Entity entity, final float eyeHeight) {
		final GetCameraEyeHeightEvent ev = new GetCameraEyeHeightEvent(entity);
		ev.setValue(eyeHeight);
		return ev;
	}
}
