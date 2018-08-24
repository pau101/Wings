package me.paulf.wings.client.winged;

import me.paulf.wings.client.model.ModelWings;
import me.paulf.wings.client.flight.Animator;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public final class WingForm<A extends Animator> {
	private final Supplier<A> animator;

	private final ModelWings<A> model;

	private final ResourceLocation texture;

	private WingForm(Supplier<A> animator, ModelWings<A> model, ResourceLocation texture) {
		this.animator = animator;
		this.model = model;
		this.texture = texture;
	}

	public A createAnimator() {
		return animator.get();
	}

	public ModelWings<A> getModel() {
		return model;
	}

	public ResourceLocation getTexture() {
		return texture;
	}

	public static <A extends Animator> WingForm<A> of(Supplier<A> animator, ModelWings<A> model, ResourceLocation texture) {
		return new WingForm<>(animator, model, texture);
	}
}
