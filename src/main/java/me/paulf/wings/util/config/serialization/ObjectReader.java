package me.paulf.wings.util.config.serialization;

import java.util.function.Function;

public interface ObjectReader {
	boolean hasNext();

	void skipValue();

	String nextName();

	char nextChar();

	long nextLong();

	double nextDouble();

	String nextString();

	<T> T nextObject(Function<ObjectReader, T> function);

	<T> T nextArray(Function<ArrayReader, T> function);

	String nextComment();

	interface ArrayReader {
		boolean hasNext();

		char nextChar();

		long nextLong();

		double nextDouble();

		String nextString();

		<T> T nextObject(Function<ObjectReader, T> function);

		<T> T nextArray(Function<ArrayReader, T> function);
	}
}
