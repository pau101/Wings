package me.paulf.wings.util;

public final class SmoothingFunction {
	private final FloatUnaryOperator easing;

	private float prevValue = Float.NaN;

	private float fromValue = Float.NaN;

	private Sign fromDirection;

	private SmoothingFunction(FloatUnaryOperator easing) {
		this.easing = easing;
	}

	public void accept(float delta, Sign direction, float value, FloatConsumer valueOut) {
		if (!Float.isNaN(prevValue) && delta != 0.0F && delta != 1.0F) {
			if (Float.isNaN(fromValue) || !fromDirection.equals(direction)) {
				fromValue = prevValue;
				fromDirection = direction;
			}
			float t = fromDirection.applyAsFloat(easing.applyAsFloat(delta));
			float newValue = fromValue + (value - fromValue) * t;
			valueOut.accept(newValue);
			prevValue = newValue;
		} else {
			fromValue = Float.NaN;
			prevValue = value;
		}
	}

	public static SmoothingFunction create(FloatUnaryOperator easing) {
		return new SmoothingFunction(easing);
	}

	public enum Sign implements FloatUnaryOperator {
		POSITIVE {
			@Override
			public float applyAsFloat(float operand) {
				return operand;
			}
		},
		NEGATIVE {
			@Override
			public float applyAsFloat(float operand) {
				return 1.0F - operand;
			}
		};

		public static Sign valueOf(boolean value) {
			return value ? POSITIVE : NEGATIVE;
		}
	}
}
