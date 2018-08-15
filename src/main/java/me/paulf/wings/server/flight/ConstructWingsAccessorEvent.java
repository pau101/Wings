package me.paulf.wings.server.flight;

import me.paulf.wings.util.ItemAccessor;
import me.paulf.wings.util.ItemPlacing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public final class ConstructWingsAccessorEvent extends Event {
	private final ItemAccessor.Builder<EntityPlayer> builder;

	public ConstructWingsAccessorEvent() {
		this(ItemAccessor.builder());
	}

	private ConstructWingsAccessorEvent(ItemAccessor.Builder<EntityPlayer> builder) {
		this.builder = builder;
	}

	public void addPlacing(ItemPlacing<EntityPlayer> placing) {
		builder.addPlacing(placing);
	}

	public ItemAccessor<EntityPlayer> build() {
		return builder.build();
	}
}
