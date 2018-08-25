package me.paulf.wings.client.flight;

import me.paulf.wings.client.apparatus.WingForm;
import me.paulf.wings.client.apparatus.FlightApparatusView;
import me.paulf.wings.client.apparatus.FlightApparatusViews;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.state.State;
import me.paulf.wings.server.flight.state.StateIdle;
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
		public void update(EntityPlayer player) {}

		@Override
		public void ifFormPresent(Consumer<FormRenderer> consumer) {}
	});

	private final SmoothingFunction eyeHeightFunc = SmoothingFunction.create(t -> Mth.easeOutCirc(Mth.easeInOut(t)));

	private WingState animator = absentAnimator;

	public FlightViewDefault(Flight flight) {
		this.flight = flight;
	}

	@Override
	public void ifFormPresent(Consumer<FormRenderer> consumer) {
		animator.ifFormPresent(consumer);
	}

	@Override
	public void onUpdate(EntityPlayer player, ItemStack wings) {
		if (!wings.isEmpty()) {
			FlightApparatusView view = FlightApparatusViews.get(wings);
			if (view == null) {
				animator = animator.next();
			} else {
				animator = animator.next(wings, view);
			}
			animator.update(player);
		}
	}

	@Override
	public void onUpdateEyeHeight(float value, float delta, FloatConsumer valueOut) {
		eyeHeightFunc.accept(flight.getFlyingAmount(delta), SmoothingFunction.Sign.valueOf(flight.isFlying()), value, valueOut);
	}

	private interface Strategy {
		void update(EntityPlayer player);

		void ifFormPresent(Consumer<FormRenderer> consumer);
	}

	private final class WingState {
		private final Item item;

		private final Strategy behavior;

		private WingState(Item item, Strategy behavior) {
			this.item = item;
			this.behavior = behavior;
		}

		private WingState next() {
			return absentAnimator;
		}

		private WingState next(ItemStack stack, FlightApparatusView view) {
			Item item = stack.getItem();
			if (this.item.equals(item)) {
				return this;
			}
			return newState(item, view.getForm());
		}

		private <T extends Animator> WingState newState(Item item, WingForm<T> shape) {
			return new WingState(item, new Strategy() {
				private final T animator = shape.createAnimator();

				private State state = new StateIdle();

				@Override
				public void update(EntityPlayer player) {
					animator.update();
					State state = this.state.update(
						flight.isFlying(),
						player.posX - player.prevPosX,
						player.posY - player.prevPosY,
						player.posZ - player.prevPosZ,
						player
					);
					if (!this.state.equals(state)) {
						state.beginAnimation(animator);
					}
					this.state = state;
				}

				@Override
				public void ifFormPresent(Consumer<FormRenderer> consumer) {
					consumer.accept(new FormRenderer() {
						@Override
						public ResourceLocation getTexture() {
							return shape.getTexture();
						}

						@Override
						public void render(float delta, float scale) {
							shape.getModel().render(animator, delta, scale);
						}
					});
				}
			});
		}

		private void update(EntityPlayer player) {
			behavior.update(player);
		}

		private void ifFormPresent(Consumer<FormRenderer> consumer) {
			behavior.ifFormPresent(consumer);
		}
	}
}
