package me.paulf.wings.client.flight;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.wings.client.apparatus.WingForm;
import me.paulf.wings.client.flight.state.State;
import me.paulf.wings.client.flight.state.StateIdle;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.util.function.FloatConsumer;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public final class FlightViewDefault implements FlightView {
    private static final WingState ABSENT_ANIMATOR = new WingState() {
        @Override
        public WingState nextAbsent() {
            return this;
        }

        @Override
        public WingState next(WingForm<?> form) {
            return PresentWingState.newState(form);
        }

        @Override
        public void update(Flight flight, PlayerEntity player) {
        }

        @Override
        public void ifFormPresent(Consumer<FormRenderer> consumer) {
        }
    };

    private final Flight flight;

    private final PlayerEntity player;

    private WingState animator = ABSENT_ANIMATOR;

    public FlightViewDefault(PlayerEntity player, Flight flight) {
        this.player = player;
        this.flight = flight;
    }

    @Override
    public void ifFormPresent(Consumer<FormRenderer> consumer) {
        this.animator.ifFormPresent(consumer);
    }

    @Override
    public void tick() {
        this.animator = WingForm.get(this.flight.getWing())
            .map(view -> this.animator.next(view))
            .orElseGet(this.animator::nextAbsent);
        this.animator.update(this.flight, this.player);
    }

    @Override
    public void tickEyeHeight(float value, FloatConsumer valueOut) {
        if (this.flight.isFlying() || (this.flight.getFlyingAmount(1.0F) > 0.0F && this.player.getPose() == Pose.FALL_FLYING)) {
            valueOut.accept(1.0F);
        }
    }

    private interface Strategy {
        void update(Flight flight, PlayerEntity player);

        void ifFormPresent(Consumer<FormRenderer> consumer);
    }

    interface WingState {
        WingState nextAbsent();

        WingState next(WingForm<?> form);

        void update(Flight flight, PlayerEntity player);

        void ifFormPresent(Consumer<FormRenderer> consumer);
    }

    private static final class PresentWingState implements WingState {
        private final WingForm<?> wing;

        private final Strategy behavior;

        private PresentWingState(WingForm<?> wing, Strategy behavior) {
            this.wing = wing;
            this.behavior = behavior;
        }

        @Override
        public WingState nextAbsent() {
            return ABSENT_ANIMATOR;
        }

        @Override
        public WingState next(WingForm<?> form) {
            if (this.wing.equals(form)) {
                return this;
            }
            return PresentWingState.newState(form);
        }

        @Override
        public void update(Flight flight, PlayerEntity player) {
            this.behavior.update(flight, player);
        }

        @Override
        public void ifFormPresent(Consumer<FormRenderer> consumer) {
            this.behavior.ifFormPresent(consumer);
        }

        public static <T extends Animator> WingState newState(WingForm<T> shape) {
            return new PresentWingState(shape, new WingStrategy<>(shape));
        }

        private static class WingStrategy<T extends Animator> implements Strategy {
            private final WingForm<T> shape;

            private final T animator;

            private State state;

            public WingStrategy(WingForm<T> shape) {
                this.shape = shape;
                this.animator = shape.createAnimator();
                this.state = new StateIdle();
            }

            @Override
            public void update(Flight flight, PlayerEntity player) {
                this.animator.update();
                State state = this.state.update(
                    flight,
                    player.getX() - player.xo,
                    player.getY() - player.yo,
                    player.getZ() - player.zo,
                    player
                );
                if (!this.state.equals(state)) {
                    state.beginAnimation(this.animator);
                }
                this.state = state;
            }

            @Override
            public void ifFormPresent(Consumer<FormRenderer> consumer) {
                consumer.accept(new FormRenderer() {
                    @Override
                    public ResourceLocation getTexture() {
                        return WingStrategy.this.shape.getTexture();
                    }

                    @Override
                    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, float delta) {
                        WingStrategy.this.shape.getModel().render(WingStrategy.this.animator, delta, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                    }
                });
            }
        }
    }
}
