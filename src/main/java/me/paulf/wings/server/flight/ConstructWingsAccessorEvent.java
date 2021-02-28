package me.paulf.wings.server.flight;

import me.paulf.wings.util.ItemAccessor;
import me.paulf.wings.util.ItemPlacing;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

public final class ConstructWingsAccessorEvent extends Event implements IModBusEvent {
	private final ItemAccessor.Builder<LivingEntity> builder;

	public ConstructWingsAccessorEvent() {
		this(ItemAccessor.builder());
	}

	private ConstructWingsAccessorEvent(final ItemAccessor.Builder<LivingEntity> builder) {
		this.builder = builder;
	}

	public void addPlacing(final ItemPlacing<LivingEntity> placing) {
		this.builder.addPlacing(placing);
	}

	public ItemAccessor<LivingEntity> build() {
		return this.builder.build();
	}
}
