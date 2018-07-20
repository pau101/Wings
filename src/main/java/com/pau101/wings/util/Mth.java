package com.pau101.wings.util;

import net.minecraft.util.math.MathHelper;

public final class Mth {
	private Mth() {}

	public static final float PI = (float) Math.PI;

	public static final float TAU = (float) (2 * Math.PI);

	private static final float DEG_TO_RAD = (float) (Math.PI / 180);

	private static final float RAD_TO_DEG = (float) (180 / Math.PI);

	public static float toRadians(float degrees) {
		return degrees * DEG_TO_RAD;
	}

	public static float toDegrees(float degrees) {
		return degrees * RAD_TO_DEG;
	}

	public static float lerp(float a, float b, float t) {
		return t <= 0 ? a : t >= 1 ? b : a + (b - a) * t;
	}

	public static float lerpDegrees(float a, float b, float t) {
		return a + t * getDifference(a, b, 360);
	}

	public static double lerpDegrees(double a, double b, double t) {
		return a + t * getDifference(a, b, 360);
	}

	private static float getDifference(float a, float b, float rot) {
		return mod(b - a + rot / 2, rot) - rot / 2;
	}

	private static double getDifference(double a, double b, double rot) {
		return mod(b - a + rot / 2, rot) - rot / 2;
	}

	public static int mod(int a, int b) {
		return (a % b + b) % b;
	}

	public static float mod(float a, float b) {
		return (a % b + b) % b;
	}

	public static double mod(double a, double b) {
		return (a % b + b) % b;
	}

	public static float easeInOut(float t) {
		return -(MathHelper.cos(PI * t) - 1) / 2;
	}

	public static float easeOutCirc(float t) {
		return MathHelper.sqrt(1 - (t - 1) * (t - 1));
	}

	public static float transform(float x, float domainMin, float domainMax, float rangeMin, float rangeMax) {
		if (x <= domainMin) {
			return rangeMin;
		}
		if (x >= domainMax) {
			return rangeMax;
		}
		return (rangeMax - rangeMin) * (x - domainMin) / (domainMax - domainMin) + rangeMin;
	}
}
