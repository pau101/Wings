package com.pau101.wings.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

public final class Reflect {
	private Reflect() {}

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	public static MethodHandle getHandle(Class<?> clazz, String[] names, Class<?>... parameterTypes) {
		Exception failed = null;
		for (String name : names) {
			try {
				Method m = clazz.getDeclaredMethod(name, parameterTypes);
				m.setAccessible(true);
				return LOOKUP.unreflect(m);
			} catch (Exception e) {
				failed = e;
			}
		}
		throw new ReflectionHelper.UnableToFindMethodException(names, failed);
	}
}
