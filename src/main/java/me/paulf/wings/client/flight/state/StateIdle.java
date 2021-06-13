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
    protected State getDescent(Flight flight, PlayerEntity player) {
        BlockPos below = new BlockPos(player.getX(), player.getY() - 0.25D, player.getZ());
        if (player.level.isEmptyBlock(below) && player.level.isEmptyBlock(below.below())) {
            return super.getDescent(flight, player);
        }
        return this.createIdle();
    }
}
