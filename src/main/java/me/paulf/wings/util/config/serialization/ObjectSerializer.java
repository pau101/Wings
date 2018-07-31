package me.paulf.wings.util.config.serialization;

public interface ObjectSerializer<T> {
	void serialize(T value, ObjectWriter writer);
}
