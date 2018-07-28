package com.pau101.wings.util.config;

public interface Property<T> {
	void set(T value);

	T get();
}
