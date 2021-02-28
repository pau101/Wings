package me.paulf.wings.client.flight.state;

import me.paulf.wings.client.flight.Animator;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.util.Mth;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public abstract class State {
	static final int STATE_DELAY = 2;

	private final int stateDelay;

	private final Consumer<Animator> animation;

	private int time;

	protected State(final Consumer<Animator> animation) {
		this(STATE_DELAY, animation);
	}

	protected State(final int stateDelay, final Consumer<Animator> animation) {
		this.stateDelay = stateDelay;
		this.animation = animation;
	}

	public final State update(final Flight flight, final double x, final double y, final double z, final PlayerEntity player, final ItemStack wings) {
		if (this.time++ > this.stateDelay) {
			return this.getNext(flight, x, y, z, player, wings);
		}
		return this;
	}

	private State getNext(final Flight flight, final double x, final double y, final double z, final PlayerEntity player, final ItemStack wings) {
		if (flight.isFlying()) {
			if (y < 0 && player.rotationPitch >= this.getPitch(x, y, z)) {
				return this.createGlide();
			}
			return this.createLift();
		}
		if (y < 0) {
			return this.getDescent(flight, player, wings);
		}
		return this.getDefault(y);
	}

	private float getPitch(final double x, final double y, final double z) {
		return Mth.toDegrees((float) -Math.atan2(y, MathHelper.sqrt(x * x + z * z)));
	}

	public final void beginAnimation(final Animator animator) {
		this.animation.accept(animator);
	}

	protected State createLand() {
		return new StateLand();
	}

	protected State createLift() {
		return new StateLift();
	}

	protected State createGlide() {
		return new StateGlide();
	}

	protected State createIdle() {
		return new StateIdle();
	}

	protected State createFall() {
		return new StateFall();
	}

	protected State getDefault(final double y) {
		return this.createIdle();
	}

	protected State getDescent(final Flight flight, final PlayerEntity player, final ItemStack wings) {
		return flight.canLand(player, wings) ? this.createLand() : this.createFall();
	}
}
