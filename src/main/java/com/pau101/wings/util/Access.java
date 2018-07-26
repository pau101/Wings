package com.pau101.wings.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public final class Access {
	private Access() {}

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	public static <T> BuilderAcceptingName<T> builder(Class<? super T> refc) {
		return new BuilderAcceptingName<>(refc);
	}

	public static final class BuilderAcceptingName<T> {
		private final Class<? super T> refc;

		private BuilderAcceptingName(Class<? super T> refc) {
			this.refc = refc;
		}

		public HandleBuilder<T> name(String name, String... others) {
			ObjectList<String> names = ObjectArrayList.wrap(new String[others.length + 1], 0);
			names.add(name);
			names.addElements(names.size(), others);
			return new HandleBuilder<>(this.refc, names);
		}
	}

	public static final class HandleBuilder<T> {
		private final Class<? super T> refc;

		private final ObjectList<String> names;

		private final ObjectArrayList<Class<?>> ptypes;

		private HandleBuilder(Class<? super T> refc, ObjectList<String> names) {
			this.refc = refc;
			this.names = names;
			this.ptypes = new ObjectArrayList<Class<?>>(new Class<?>[4], false) {
				// create a trim that preserves type
				@Override
				public void trim() {
					a = ObjectArrays.trim(a, size);
				}
			};
		}

		public HandleBuilder<T> ptype(Class<?> ptype) {
			this.ptypes.add(ptype);
			return this;
		}

		public HandleBuilder<T> ptypes(Class<?>... ptypes) {
			this.ptypes.addElements(this.ptypes.size(), ptypes);
			return this;
		}

		public <R> MethodHandle rtype(Class<R> rtype) {
			this.ptypes.trim();
			return find(this.refc, this.names, MethodType.methodType(rtype, this.ptypes.elements()));
		}

		private static MethodHandle find(Class<?> refc, ObjectList<String> names, MethodType type) {
			Class<?>[] parameterTypes = type.parameterArray();
			for (ObjectListIterator<String> it = names.iterator(); ; ) {
				String name = it.next();
				// TODO: verbose exception message
				try {
					Method m = refc.getDeclaredMethod(name, parameterTypes);
					m.setAccessible(true);
					if (m.getReturnType() != type.returnType()) {
						throw new NoSuchMethodException();
					}
					return LOOKUP.unreflect(m);
				} catch (NoSuchMethodException | IllegalAccessException e) {
					if (!it.hasNext()) {
						throw new ReflectionHelper.UnableToFindMethodException(e);
					}
				}
			}
		}
	}

	public static <T extends Throwable> RuntimeException rethrow(Throwable t) throws T {
		//noinspection unchecked
		throw (T) t;
	}
}
