package me.paulf.wings.util;

import me.paulf.wings.util.function.FloatConsumer;
import me.paulf.wings.util.function.FloatUnaryOperator;

public final class SmoothingFunction {
	private final FloatUnaryOperator easing;

	private float prevValue = Float.NaN;

	private float fromValue = Float.NaN;

	private Sign fromDirection;

	private SmoothingFunction(final FloatUnaryOperator easing) {
		this.easing = easing;
	}

	public void accept(final float delta, final Sign direction, final float value, final FloatConsumer valueOut) {
		if (!Float.isNaN(this.prevValue) && delta != 0.0F && delta != 1.0F) {
			if (Float.isNaN(this.fromValue) || !this.fromDirection.equals(direction)) {
				this.fromValue = this.prevValue;
				this.fromDirection = direction;
			}
			final float t = this.fromDirection.applyAsFloat(this.easing.applyAsFloat(delta));
			final float newValue = this.fromValue + (value - this.fromValue) * t;
			valueOut.accept(newValue);
			this.prevValue = newValue;
		} else {
			this.fromValue = Float.NaN;
			this.prevValue = value;
		}
	}

	public static SmoothingFunction create(final FloatUnaryOperator easing) {
		return new SmoothingFunction(easing);
	}

	public enum Sign implements FloatUnaryOperator {
		POSITIVE {
			@Override
			public float applyAsFloat(final float operand) {
				return operand;
			}
		},
		NEGATIVE {
			@Override
			public float applyAsFloat(final float operand) {
				return 1.0F - operand;
			}
		};

		public static Sign valueOf(final boolean value) {
			return value ? POSITIVE : NEGATIVE;
		}
	}
}
