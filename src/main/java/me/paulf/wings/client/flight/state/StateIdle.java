package me.paulf.wings.client.flight.state;

import me.paulf.wings.client.flight.Animator;
import me.paulf.wings.server.flight.Flight;
import net.minecraft.entity.player.PlayerEntity;
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
	protected State getDescent(final Flight flight, final PlayerEntity player) {
		final BlockPos below = new BlockPos(player.getPosX(), player.getPosY() - 0.25D, player.getPosZ());
		if (player.world.isAirBlock(below) && player.world.isAirBlock(below.down())) {
			return super.getDescent(flight, player);
		}
		return this.createIdle();
	}
}
