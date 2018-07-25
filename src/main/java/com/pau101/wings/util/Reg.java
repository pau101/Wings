package com.pau101.wings.util;

import java.util.function.Consumer;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.registries.IForgeRegistryEntry;

public final class Reg {
	private Reg() {}

	private static final Converter<String, String> UNDERSCORE_TO_CAMEL = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL);

	private static String underScoreToCamel(String value) {
		return UNDERSCORE_TO_CAMEL.convert(value);
	}

	static <T extends IForgeRegistryEntry.Impl<? super T>> T withName(T entry, String registryName, Consumer<String> translationKeySetter) {
		entry.setRegistryName(registryName);
		translationKeySetter.accept(underScoreToCamel(registryName));
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
