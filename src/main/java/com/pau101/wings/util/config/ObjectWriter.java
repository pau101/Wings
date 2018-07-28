package com.pau101.wings.util.config;

import java.util.function.Consumer;

public interface ObjectWriter {
	ObjectWriter put(String key, String value);

	ObjectWriter put(String key, char value);

	ObjectWriter put(String key, long value);

	ObjectWriter put(String key, double value);

	ObjectWriter putObject(String key, Consumer<ObjectWriter> mediator);

	ObjectWriter putArray(String key, Consumer<ArrayWriter> mediator);

	ObjectWriter comment(String message);

	interface ArrayWriter {
		ArrayWriter add(String value);

		ArrayWriter add(char value);

		ArrayWriter add(long value);

		ArrayWriter add(double value);

		ArrayWriter addObject(Consumer<ArrayWriter> mediator);

		ArrayWriter addArray(Consumer<ArrayWriter> mediator);
	}
}
