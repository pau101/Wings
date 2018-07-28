package com.pau101.wings.util.config;

public interface ObjectSerializer<T> {
	void serialize(T value, ObjectWriter writer);
}
