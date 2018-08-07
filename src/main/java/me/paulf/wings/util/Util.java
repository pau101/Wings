package me.paulf.wings.util;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public final class Util {
	private Util() {}

	private static final Converter<String, String> UNDERSCORE_TO_CAMEL = CaseFormat.LOWER_UNDERSCORE
		.converterTo(CaseFormat.LOWER_CAMEL);

	public static String underScoreToCamel(String value) {
		return UNDERSCORE_TO_CAMEL.convert(value);
	}

	public static ResourceLocation getName(IForgeRegistryEntry<?> entry) {
		ResourceLocation name = entry.getRegistryName();
		if (name == null) {
			throw new NullPointerException("Missing registry name: " + entry);
		}
		return name;
	}

	public static Block setHarvestLevel(Block block, HarvestClass harvestClass, HarvestLevel harvestLevel) {
		block.setHarvestLevel(harvestClass.getName(), harvestLevel.getValue());
		return block;
	}

	public static Item requireItem(ResourceLocation id) {
		return require(ForgeRegistries.ITEMS, id);
	}

	private static <V extends IForgeRegistryEntry<V>> V require(IForgeRegistry<V> registry, ResourceLocation id) {
		V v = registry.containsKey(id) ? registry.getValue(id) : null;
		if (v == null) {
			throw new IllegalStateException("Missing registry object: " + id);
		}
		return v;
	}
}
