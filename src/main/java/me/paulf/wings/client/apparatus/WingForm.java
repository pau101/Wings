package me.paulf.wings.client.apparatus;

import me.paulf.wings.client.flight.Animator;
import me.paulf.wings.client.flight.AnimatorAvian;
import me.paulf.wings.client.model.ModelWings;
import me.paulf.wings.server.apparatus.FlightApparatus;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class WingForm<A extends Animator> {
    private static final Map<FlightApparatus, WingForm<?>> FORMS = new HashMap<>();

    private final Supplier<A> animator;

    private final ModelWings<A> model;

    private final ResourceLocation texture;

    private WingForm(Supplier<A> animator, ModelWings<A> model, ResourceLocation texture) {
        this.animator = animator;
        this.model = model;
        this.texture = texture;
    }

    public A createAnimator() {
        return this.animator.get();
    }

    public ModelWings<A> getModel() {
        return this.model;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public static <A extends Animator> WingForm<A> of(Supplier<A> animator, ModelWings<A> model, ResourceLocation texture) {
        return new WingForm<>(animator, model, texture);
    }

    public static Optional<WingForm<?>> get(FlightApparatus wings) {
        return Optional.ofNullable(FORMS.get(wings));
    }

    public static void register(FlightApparatus wings, WingForm<?> form) {
        FORMS.put(wings, form);
    }
}
