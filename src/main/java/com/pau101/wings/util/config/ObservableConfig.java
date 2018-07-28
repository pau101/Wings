package com.pau101.wings.util.config;

import com.google.common.base.MoreObjects;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectCollections;

public abstract class ObservableConfig {
	private final Object2ObjectMap<String, Property<?>> properties = new Object2ObjectOpenHashMap<>();

	protected <T> PropertyBuilder<T> property(String name, T defaultValue) {
		return new PropertyBuilder<>(name, defaultValue);
	}

	public ObjectCollection<? extends Property<?>> properties() {
		return ObjectCollections.unmodifiable(this.properties.values());
	}

	private final class ConfigProperty<T> implements Property<T> {
		private final String name;

		private final T defaultValue;

		private final String comment;

		private T value;

		private ConfigProperty(String name, T defaultValue, String comment) {
			this.name = name;
			this.defaultValue = defaultValue;
			this.comment = comment;
		}

		@Override
		public void set(T value) {
			this.value = value; 
		}

		@Override
		public T get() {
			return this.value;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
				.add("name", this.name)
				.add("defaultValue", this.defaultValue)
				.add("comment", this.comment)
				.add("value", this.value)
				.toString();
		}
	}

	protected final class PropertyBuilder<T> {
		private final String name;

		private final T defaultValue;

		private String comment;

		private PropertyBuilder(String name, T defaultValue) {
			this.name = name;
			this.defaultValue = defaultValue;
		}

		public PropertyBuilder<T> comment(String comment) {
			this.comment = comment;
			return this;
		}

		public Property<T> add() {
			Property<T> property = new ConfigProperty<>(this.name, this.defaultValue, this.comment);
			if (ObservableConfig.this.properties.put(this.name, property) != null) {
				throw new IllegalStateException("Duplicate property");
			}
			property.set(this.defaultValue);
			return property;
		}
	}
}
