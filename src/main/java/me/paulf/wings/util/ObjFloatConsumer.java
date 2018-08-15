package me.paulf.wings.util;

@FunctionalInterface
public interface ObjFloatConsumer<T> {
    void accept(T t, float value);
}
