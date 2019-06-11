package me.paulf.wings.util;

public enum HarvestClass {
	PICKAXE("pickaxe"),
	AXE("axe"),
	SHOVEL("shovel");

	private final String name;

	HarvestClass(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
