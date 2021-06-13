package me.paulf.wings.util.function;

@FunctionalInterface
public interface FloatUnaryOperator {
    float applyAsFloat(float operand);

    static FloatUnaryOperator identity() {
        return t -> t;
    }
}
