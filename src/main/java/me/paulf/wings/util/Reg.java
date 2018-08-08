package me.paulf.wings.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Consumer;

public final class Reg {
	private Reg() {}

	static <T extends IForgeRegistryEntry.Impl<? super T>> T withName(
		T entry, String registryName, Consumer<String> translationKeySetter
	) {
		entry.setRegistryName(registryName);
		translationKeySetter.accept(Util.getName(entry).getNamespace() + "." + Util.underScoreToCamel(registryName));
		return entry;
	}

	public static <I extends Item> I withName(I item, String registryName) {
		return withName(item, registryName, item::setTranslationKey);
	}

	public static <B extends Block> B withName(B block, String registryName) {
		return withName(block, registryName, block::setTranslationKey);
	}

	public static Item createItem(Block block) {
		return new ItemBlock(block).setRegistryName(Util.getName(block));
	}
}
