package com.pau101.wings.server.flight.state;

import com.pau101.wings.server.flight.Animator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public final class StateIdle extends State {
	public StateIdle() {
		super(Animator::beginIdle);
	}

	@Override
	protected State getIdle() {
		return this;
	}

	@Override
	protected State getFalling(EntityPlayer player) {
		BlockPos below = new BlockPos(player.posX, player.posY - 0.25D, player.posZ);
		if (player.world.isAirBlock(below) && player.world.isAirBlock(below.down())) {
			return super.getFalling(player);
		}
		return getIdle();
	}
}
