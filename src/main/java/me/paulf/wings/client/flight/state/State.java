package me.paulf.wings.client.flight.state;

import me.paulf.wings.client.flight.Animator;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.util.Mth;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public abstract class State {
	static final int STATE_DELAY = 2;

	private final int stateDelay;

	private final Consumer<Animator> animation;

	private int time;

	protected State(Consumer<Animator> animation) {
		this(STATE_DELAY, animation);
	}

	protected State(int stateDelay, Consumer<Animator> animation) {
		this.stateDelay = stateDelay;
		this.animation = animation;
	}

	public final State update(Flight flight, double x, double y, double z, EntityPlayer player, ItemStack wings) {
		if (time++ > stateDelay) {
			return getNext(flight, x, y, z, player, wings);
		}
		return this;
	}

	private State getNext(Flight flight, double x, double y, double z, EntityPlayer player, ItemStack wings) {
		if (flight.isFlying()) {
			if (y < 0 && player.rotationPitch >= getPitch(x, y, z)) {
				return createGlide();
			}
			return createLift();
		}
		if (y < 0) {
			return getDescent(flight, player, wings);
		}
		return getDefault(y);
	}

	private float getPitch(double x, double y, double z) {
		return Mth.toDegrees((float) -Math.atan2(y, MathHelper.sqrt(x * x + z * z)));
	}

	public final void beginAnimation(Animator animator) {
		animation.accept(animator);
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

	protected State getDefault(double y) {
		return createIdle();
	}

	protected State getDescent(Flight flight, EntityPlayer player, ItemStack wings) {
		return flight.canLand(player, wings) ? createLand() : createFall();
	}
}
