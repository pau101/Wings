package me.paulf.wings.util.config.serialization;

import java.util.function.Consumer;

public interface ObjectWriter {
	ObjectWriter name(String name);

	ObjectWriter value(char value);

	ObjectWriter value(long value);

	ObjectWriter value(double value);

	ObjectWriter value(String value);

	ObjectWriter object(Consumer<ObjectWriter> consumer);

	ObjectWriter array(Consumer<ArrayWriter> consumer);

	ObjectWriter comment(String message);

	interface ArrayWriter {
		ArrayWriter value(char value);

		ArrayWriter value(long value);

		ArrayWriter value(double value);

		ArrayWriter value(String value);

		ArrayWriter object(Consumer<ArrayWriter> consumer);

		ArrayWriter array(Consumer<ArrayWriter> consumer);
	}
}
