package me.paulf.wings.client.flight;

import me.paulf.wings.server.flight.Animator;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.state.State;
import me.paulf.wings.server.flight.state.StateIdle;
import me.paulf.wings.server.item.ItemWings;
import me.paulf.wings.util.Mth;
import me.paulf.wings.util.SmoothingFunction;
import me.paulf.wings.util.function.FloatConsumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public final class FlightViewDefault implements FlightView {
	private final Flight flight;

	private State state = new StateIdle();

	private Animator animator = Animator.ABSENT;

	private final SmoothingFunction eyeHeightFunc = SmoothingFunction.create(t -> Mth.easeOutCirc(Mth.easeInOut(t)));

	public FlightViewDefault(Flight flight) {
		this.flight = flight;
	}

	@Override
	public Vec3d getWingRotation(int index, float delta) {
		return animator.getWingRotation(index, delta);
	}

	@Override
	public Vec3d getFeatherRotation(int index, float delta) {
		return animator.getFeatherRotation(index, delta);
	}

	@Override
	public void onUpdate(EntityPlayer player, ItemStack wings) {
		if (!wings.isEmpty()) {
			double dx = player.posX - player.prevPosX;
			double dy = player.posY - player.prevPosY;
			double dz = player.posZ - player.prevPosZ;
			animator = ItemWings.getType(wings).getAnimator(animator);
			animator.update(dx, dy, dz);
			State state = this.state.update(flight.isFlying(), dx, dy, dz, player);
			if (!this.state.equals(state)) {
				state.beginAnimation(animator);
			}
			this.state = state;
		}
	}

	@Override
	public void onUpdateEyeHeight(float value, float delta, FloatConsumer valueOut) {
		eyeHeightFunc.accept(flight.getFlyingAmount(delta), SmoothingFunction.Sign.valueOf(flight.isFlying()), value, valueOut);
	}
}
