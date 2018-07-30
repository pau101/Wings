package me.paulf.wings.util;

@FunctionalInterface
public interface FloatUnaryOperator {
    float applyAsFloat(float operand);

    static FloatUnaryOperator identity() {
    	return t -> t;
	}
}
