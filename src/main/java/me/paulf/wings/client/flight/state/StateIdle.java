package me.paulf.wings.client.flight.state;

import me.paulf.wings.client.flight.Animator;
import me.paulf.wings.server.flight.Flight;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public final class StateIdle extends State {
	public StateIdle() {
		super(Animator::beginIdle);
	}

	@Override
	protected State createIdle() {
		return this;
	}

	@Override
	protected State getDescent(final Flight flight, final EntityPlayer player, final ItemStack wings) {
		final BlockPos below = new BlockPos(player.posX, player.posY - 0.25D, player.posZ);
		if (player.world.isAirBlock(below) && player.world.isAirBlock(below.down())) {
			return super.getDescent(flight, player, wings);
		}
		return this.createIdle();
	}
}
