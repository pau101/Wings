package com.pau101.wings.server.flight;

import com.pau101.wings.server.flight.animator.AnimatorAvian;
import com.pau101.wings.server.flight.animator.AnimatorInsectoid;

import java.util.function.Predicate;
import java.util.function.Supplier;

public enum StandardAnimatorFactory implements AnimatorFactory {
	AVIAN(AnimatorAvian.class, AnimatorAvian::new),
	INSECTOID(AnimatorInsectoid.class, AnimatorInsectoid::new);

	private final Predicate<Animator> predicate;

	private final Supplier<? extends Animator> factory;

	<A extends Animator> StandardAnimatorFactory(Class<? super A> cls, Supplier<A> factory) {
		this.predicate = a -> cls.isAssignableFrom(a.getClass());
		this.factory = factory;
	}

	@Override
	public final boolean provides(Animator animator) {
		return predicate.test(animator);
	}

	@Override
	public final Animator create() {
		return factory.get();
	}
}
