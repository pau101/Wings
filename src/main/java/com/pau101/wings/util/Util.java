package com.pau101.wings.util;

import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistryEntry;

@SuppressWarnings({ "unused", "WeakerAccess" })
public final class Util {
	private Util() {}

	private static final Converter<String, String> UNDRSCR_TO_CML = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL);

	public static String underScoreToCamel(String value) {
		return UNDRSCR_TO_CML.convert(value);
	}

	public static <I extends Item> I name(I item, String registryName) {
		return name(item, registryName, item::setUnlocalizedName);
	}

	public static <B extends Block> B name(B block, String registryName) {
		return name(block, registryName, block::setUnlocalizedName);
	}

	private static <T extends IForgeRegistryEntry.Impl<? super T>> T name(T entry, String registryName, Consumer<String> unlocalizedNameSetter) {
		entry.setRegistryName(registryName);
		unlocalizedNameSetter.accept(underScoreToCamel(registryName));
		return entry;
	}

	public static <S> void ifPlayer(@Nullable S object, Consumer<? super EntityPlayer> action) {
		ifIs(object, EntityPlayer.class, action);
	}

	public static <S> void ifPlayer(@Nullable S object, Predicate<S> condition, Consumer<? super EntityPlayer> action) {
		ifIs(object, condition, EntityPlayer.class, action);
	}

	public static <S, T> void ifIs(@Nullable S object, Class<T> typeClass, Consumer<? super T> action) {
		ifIs(object, s -> true, typeClass, action);
	}

	public static <S, T> void ifIs(@Nullable S object, Predicate<S> condition, Class<T> typeClass, Consumer<? super T> action) {
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
