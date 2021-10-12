package me.paulf.wings.client.flight;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.wings.WingsMod;
import me.paulf.wings.client.apparatus.WingForm;
import me.paulf.wings.client.flight.state.State;
import me.paulf.wings.client.flight.state.StateIdle;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.util.function.FloatConsumer;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;
import java.util.function.Consumer;

public final class FlightViewDefault implements FlightView {
    private final Flight flight;

    private final WingState absentAnimator = new WingState(
        WingForm.get(WingsMod.WINGS.get(WingsMod.Names.ANGEL))
            .orElseThrow(IllegalStateException::new),
        new Strategy() {
            @Override
            public void update(PlayerEntity player) {
            }

            @Override
            public void ifFormPresent(Consumer<FormRenderer> consumer) {
            }
        });

    private final PlayerEntity player;

    private WingState animator = this.absentAnimator;

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
        this.animator.update(this.player);
    }

    @Override
    public void tickEyeHeight(float value, FloatConsumer valueOut) {
        if (this.flight.isFlying() || (this.flight.getFlyingAmount(1.0F) > 0.0F && this.player.getPose() == Pose.FALL_FLYING)) {
            valueOut.accept(1.0F);
        }
    }

    private interface Strategy {
        void update(PlayerEntity player);

        void ifFormPresent(Consumer<FormRenderer> consumer);
    }

    private final class WingState {
        private final WingForm<?> wing;

        private final Strategy behavior;

        private WingState(WingForm<?> wing, Strategy behavior) {
            this.wing = wing;
            this.behavior = behavior;
        }

        private WingState nextAbsent() {
            return FlightViewDefault.this.absentAnimator;
        }

        private WingState next(WingForm<?> form) {
            if (this.wing.equals(form)) {
                return this;
            }
            return this.newState(form);
        }

        private <T extends Animator> WingState newState(WingForm<T> shape) {
            return new WingState(shape, new WingStrategy<>(shape));
        }

        private void update(PlayerEntity player) {
            this.behavior.update(player);
        }

        private void ifFormPresent(Consumer<FormRenderer> consumer) {
            this.behavior.ifFormPresent(consumer);
        }

        private class WingStrategy<T extends Animator> implements Strategy {
            private final WingForm<T> shape;

            private final T animator;

            private State state;

            public WingStrategy(WingForm<T> shape) {
                this.shape = shape;
                this.animator = shape.createAnimator();
                this.state = new StateIdle();
            }

            @Override
            public void update(PlayerEntity player) {
                this.animator.update();
                State state = this.state.update(
                    FlightViewDefault.this.flight,
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
