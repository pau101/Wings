package me.paulf.wings.util;

public enum HarvestLevel {
	NONE(-1),
	WOOD(0),
	GOLD(0),
	STONE(1),
	IRON(2),
	DIAMOND(3);

	private final int value;

	HarvestLevel(final int value) {
		this.value = value;
	}

	public final int getValue() {
		return this.value;
	}
}
