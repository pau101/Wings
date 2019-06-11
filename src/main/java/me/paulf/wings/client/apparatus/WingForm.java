package me.paulf.wings.client.apparatus;

import me.paulf.wings.client.model.ModelWings;
import me.paulf.wings.client.flight.Animator;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public final class WingForm<A extends Animator> {
	private final Supplier<A> animator;

	private final ModelWings<A> model;

	private final ResourceLocation texture;

	private WingForm(final Supplier<A> animator, final ModelWings<A> model, final ResourceLocation texture) {
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

	public static <A extends Animator> WingForm<A> of(final Supplier<A> animator, final ModelWings<A> model, final ResourceLocation texture) {
		return new WingForm<>(animator, model, texture);
	}
}
