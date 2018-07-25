package com.pau101.wings.util;

import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.registries.IForgeRegistryEntry;

public final class Reg {
	private Reg() {}

	static <T extends IForgeRegistryEntry.Impl<? super T>> T withName(T entry, String registryName, Consumer<String> translationKeySetter) {
		entry.setRegistryName(registryName);
		translationKeySetter.accept(Util.underScoreToCamel(registryName));
		return entry;
	}

	public static <I extends Item> I withName(I item, String registryName) {
		return withName(item, registryName, item::setTranslationKey);
	}

	public static <B extends Block> B withName(B block, String registryName) {
		return withName(block, registryName, block::setTranslationKey);
	}

	public static Item asItem(Block block) {
		return new ItemBlock(block).setRegistryName(Util.getName(block));
	}
}
