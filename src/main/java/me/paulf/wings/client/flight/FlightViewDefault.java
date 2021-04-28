package me.paulf.wings.client.flight;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.wings.client.apparatus.FlightApparatusView;
import me.paulf.wings.client.apparatus.FlightApparatusViews;
import me.paulf.wings.client.apparatus.WingForm;
import me.paulf.wings.client.flight.state.State;
import me.paulf.wings.client.flight.state.StateIdle;
import me.paulf.wings.server.apparatus.FlightApparatuses;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.item.WingsItems;
import me.paulf.wings.util.function.FloatConsumer;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public final class FlightViewDefault implements FlightView {
	private final Flight flight;

	private final WingState absentAnimator = new WingState(WingsItems.Names.ANGEL, new Strategy() {
		@Override
		public void update(final PlayerEntity player) {}

		@Override
		public void ifFormPresent(final Consumer<FormRenderer> consumer) {}
	});

	private final PlayerEntity player;

	private WingState animator = this.absentAnimator;

	public FlightViewDefault(final PlayerEntity player, final Flight flight) {
		this.player = player;
		this.flight = flight;
	}

	@Override
	public void ifFormPresent(final Consumer<FormRenderer> consumer) {
		this.animator.ifFormPresent(consumer);
	}

	@Override
	public void tick() {
		this.animator = FlightApparatusViews.get(wings)
			.map(view -> this.animator.next(wings, view))
			.orElseGet(this.animator::next);
		this.animator.update(this.player);
	}

	@Override
	public void tickEyeHeight(final float value, final FloatConsumer valueOut) {
		if (this.flight.isFlying() || (this.flight.getFlyingAmount(1.0F) > 0.0F && this.player.getPose() == Pose.FALL_FLYING)) {
			valueOut.accept(1.0F);
		}
	}

	private interface Strategy {
		void update(PlayerEntity player);

		void ifFormPresent(Consumer<FormRenderer> consumer);
	}

	private final class WingState {
		private final ResourceLocation wing;

		private final Strategy behavior;

		private WingState(final ResourceLocation wing, final Strategy behavior) {
			this.wing = wing;
			this.behavior = behavior;
		}

		private WingState next() {
			return FlightViewDefault.this.absentAnimator;
		}

		private WingState next(final ResourceLocation wing, final FlightApparatusView view) {
			if (this.wing.equals(wing)) {
				return this;
			}
			return this.newState(this.wing, view.getForm());
		}

		private <T extends Animator> WingState newState(final ResourceLocation wing, final WingForm<T> shape) {
			return new WingState(wing, new WingStrategy<>(shape));
		}

		private void update(final PlayerEntity player) {
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
			public void update(final PlayerEntity player) {
				this.animator.update();
				final State state = this.state.update(
					FlightViewDefault.this.flight,
					player.getPosX() - player.prevPosX,
					player.getPosY() - player.prevPosY,
					player.getPosZ() - player.prevPosZ,
					player
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
					public void render(final MatrixStack matrixStack, final IVertexBuilder buffer, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha, final float delta) {
						WingStrategy.this.shape.getModel().render(WingStrategy.this.animator, delta, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
					}
				});
			}
		}
	}
}
