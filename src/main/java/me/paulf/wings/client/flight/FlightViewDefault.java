package me.paulf.wings.client.flight;

import me.paulf.wings.client.apparatus.FlightApparatusView;
import me.paulf.wings.client.apparatus.FlightApparatusViews;
import me.paulf.wings.client.apparatus.WingForm;
import me.paulf.wings.client.flight.state.State;
import me.paulf.wings.client.flight.state.StateIdle;
import me.paulf.wings.server.apparatus.FlightApparatuses;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.util.Mth;
import me.paulf.wings.util.SmoothingFunction;
import me.paulf.wings.util.function.FloatConsumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public final class FlightViewDefault implements FlightView {
	private final Flight flight;

	private final WingState absentAnimator = new WingState(Items.AIR, new Strategy() {
		@Override
		public void update(final EntityPlayer player) {}

		@Override
		public void ifFormPresent(final Consumer<FormRenderer> consumer) {}
	});

	private final SmoothingFunction eyeHeightFunc = SmoothingFunction.create(t -> Mth.easeOutCirc(Mth.easeInOut(t)));

	private WingState animator = this.absentAnimator;

	public FlightViewDefault(final Flight flight) {
		this.flight = flight;
	}

	@Override
	public void ifFormPresent(final Consumer<FormRenderer> consumer) {
		this.animator.ifFormPresent(consumer);
	}

	@Override
	public void tick(final EntityPlayer player, final ItemStack wings) {
		if (!wings.isEmpty()) {
			final FlightApparatusView view = FlightApparatusViews.get(wings);
			if (view == null) {
				this.animator = this.animator.next();
			} else {
				this.animator = this.animator.next(wings, view);
			}
			this.animator.update(player);
		}
	}

	@Override
	public void tickEyeHeight(final float value, final float delta, final FloatConsumer valueOut) {
		this.eyeHeightFunc.accept(this.flight.getFlyingAmount(delta), SmoothingFunction.Sign.valueOf(this.flight.isFlying()), value, valueOut);
	}

	private interface Strategy {
		void update(EntityPlayer player);

		void ifFormPresent(Consumer<FormRenderer> consumer);
	}

	private final class WingState {
		private final Item item;

		private final Strategy behavior;

		private WingState(final Item item, final Strategy behavior) {
			this.item = item;
			this.behavior = behavior;
		}

		private WingState next() {
			return FlightViewDefault.this.absentAnimator;
		}

		private WingState next(final ItemStack stack, final FlightApparatusView view) {
			final Item item = stack.getItem();
			if (this.item.equals(item)) {
				return this;
			}
			return this.newState(item, view.getForm());
		}

		private <T extends Animator> WingState newState(final Item item, final WingForm<T> shape) {
			return new WingState(item, new WingStrategy<>(shape));
		}

		private void update(final EntityPlayer player) {
			this.behavior.update(player);
		}

		private void ifFormPresent(final Consumer<FormRenderer> consumer) {
			this.behavior.ifFormPresent(consumer);
		}

		private class WingStrategy<T extends Animator> implements Strategy {
			private final WingForm<T> shape;

			private final T animator;

			private State state;

			public WingStrategy(final WingForm<T> shape) {
				this.shape = shape;
				this.animator = shape.createAnimator();
				this.state = new StateIdle();
			}

			@Override
			public void update(final EntityPlayer player) {
				this.animator.update();
				final State state = this.state.update(
					FlightViewDefault.this.flight,
					player.posX - player.prevPosX,
					player.posY - player.prevPosY,
					player.posZ - player.prevPosZ,
					player,
					FlightApparatuses.find(player)
				);
				if (!this.state.equals(state)) {
					state.beginAnimation(this.animator);
				}
				this.state = state;
			}

			@Override
			public void ifFormPresent(final Consumer<FormRenderer> consumer) {
				consumer.accept(new FormRenderer() {
					@Override
					public ResourceLocation getTexture() {
						return WingStrategy.this.shape.getTexture();
					}

					@Override
					public void render(final float delta, final float scale) {
						WingStrategy.this.shape.getModel().render(WingStrategy.this.animator, delta, scale);
					}
				});
			}
		}
	}
}
