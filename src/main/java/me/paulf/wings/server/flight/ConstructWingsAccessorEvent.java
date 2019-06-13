package me.paulf.wings.server.flight;

import me.paulf.wings.util.ItemAccessor;
import me.paulf.wings.util.ItemPlacing;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Event;

public final class ConstructWingsAccessorEvent extends Event {
	private final ItemAccessor.Builder<EntityLivingBase> builder;

	public ConstructWingsAccessorEvent() {
		this(ItemAccessor.builder());
	}

	private ConstructWingsAccessorEvent(final ItemAccessor.Builder<EntityLivingBase> builder) {
		this.builder = builder;
	}

	public void addPlacing(final ItemPlacing<EntityLivingBase> placing) {
		this.builder.addPlacing(placing);
	}

	public ItemAccessor<EntityLivingBase> build() {
		return this.builder.build();
	}
}
