package com.pau101.wings.util;

import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public final class Util {
	private Util() {}

	private static final Converter<String, String> UNDERSCORE_TO_CAMEL = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL);

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

	public static <S> void ifPlayer(@Nullable S object, Consumer<? super EntityPlayer> action) {
		ifOfType(object, EntityPlayer.class, action);
	}

	public static <S> void ifPlayer(@Nullable S object, Predicate<S> condition, Consumer<? super EntityPlayer> action) {
		ifOfType(object, condition, EntityPlayer.class, action);
	}

	private static <S, T> void ifOfType(@Nullable S object, Class<T> typeClass, Consumer<? super T> action) {
		ifOfType(object, s -> true, typeClass, action);
	}

	private static <S, T> void ifOfType(@Nullable S object, Predicate<S> condition, Class<T> typeClass, Consumer<? super T> action) {
		if (object != null && typeClass.isAssignableFrom(object.getClass()) && condition.test(object)) {
			//noinspection unchecked
			action.accept((T) object);
		}
	}

	public static Block setHarvestLevel(Block block, HarvestClass harvestClass, HarvestLevel harvestLevel) {
		block.setHarvestLevel(harvestClass.getName(), harvestLevel.getValue());
		return block;
	}
}
