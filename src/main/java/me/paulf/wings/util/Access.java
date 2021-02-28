package me.paulf.wings.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BiFunction;

public final class Access {
	private Access() {}

	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	public static <T> NamingVirtualHandleBuilder<T> virtual(final Class<T> refc) {
		return new NamingVirtualHandleBuilder<>(refc);
	}

	public static <T> NamingGetterHandleBuilder<T> getter(final Class<T> refc) {
		return new NamingGetterHandleBuilder<>(refc);
	}

	private static abstract class NamingBuilder<T, F> {
		private final BiFunction<Class<T>, ObjectArrayList<String>, F> factory;

		private final Class<T> refc;

		private NamingBuilder(final BiFunction<Class<T>, ObjectArrayList<String>, F> factory, final Class<T> refc) {
			this.factory = factory;
			this.refc = refc;
		}

		F name(final String name, final String... others) {
			final ObjectArrayList<String> names = ObjectArrayList.wrap(new String[others.length + 1], 0);
			names.add(name);
			names.addElements(names.size(), others);
			return this.factory.apply(this.refc, names);
		}
	}

	private static abstract class HandleBuilder<T> {
		final Class<T> refc;

		final ObjectArrayList<String> names;

		private HandleBuilder(final Class<T> refc, final ObjectArrayList<String> names) {
			this.refc = refc;
			this.names = names;
		}
	}

	public static final class NamingVirtualHandleBuilder<T> extends NamingBuilder<T, VirtualHandleBuilder<T>> {
		private NamingVirtualHandleBuilder(final Class<T> refc) {
			super(VirtualHandleBuilder::new, refc);
		}

		@Override
		public VirtualHandleBuilder<T> name(final String name, final String... others) {
			return super.name(name, others);
		}
	}

	public static final class VirtualHandleBuilder<T> extends HandleBuilder<T> {
		private final ObjectArrayList<Class<?>> ptypes;

		private VirtualHandleBuilder(final Class<T> refc, final ObjectArrayList<String> names) {
			super(refc, names);
			this.ptypes = new ObjectArrayList<Class<?>>(new Class<?>[4], false) {
				// create a trim that preserves type
				@Override
				public void trim() {
					this.a = ObjectArrays.trim(this.a, this.size);
				}
			};
		}

		public VirtualHandleBuilder<T> ptype(final Class<?> ptype) {
			this.ptypes.add(ptype);
			return this;
		}

		public VirtualHandleBuilder<T> ptypes(final Class<?>... ptypes) {
			this.ptypes.addElements(this.ptypes.size(), ptypes);
			return this;
		}

		public <R> MethodHandle rtype(final Class<R> rtype) {
			this.ptypes.trim();
			return find(this.refc, this.names, MethodType.methodType(rtype, this.ptypes.elements()));
		}

		private static MethodHandle find(final Class<?> refc, final ObjectArrayList<String> names, final MethodType type) {
			final Class<?>[] parameterTypes = type.parameterArray();
			for (final ObjectListIterator<String> it = names.iterator(); ; ) {
				final String name = it.next();
				// TODO: verbose exception message
				try {
					final Method m = refc.getDeclaredMethod(name, parameterTypes);
					m.setAccessible(true);
					if (m.getReturnType() != type.returnType()) {
						throw new NoSuchMethodException();
					}
					return LOOKUP.unreflect(m);
				} catch (final NoSuchMethodException | IllegalAccessException e) {
					if (!it.hasNext()) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	public static final class NamingGetterHandleBuilder<T> extends NamingBuilder<T, GetterHandleBuilder<T>> {
		private NamingGetterHandleBuilder(final Class<T> refc) {
			super(GetterHandleBuilder::new, refc);
		}

		@Override
		public GetterHandleBuilder<T> name(final String name, final String... others) {
			return super.name(name, others);
		}
	}

	public static final class GetterHandleBuilder<T> extends HandleBuilder<T> {
		private GetterHandleBuilder(final Class<T> refc, final ObjectArrayList<String> names) {
			super(refc, names);
		}

		public <R> MethodHandle type(final Class<R> type) {
			return find(this.refc, this.names, MethodType.methodType(type, this.refc));
		}

		private static MethodHandle find(final Class<?> refc, final ObjectArrayList<String> names, final MethodType type) {
			for (final ObjectListIterator<String> it = names.iterator(); ; ) {
				final String name = it.next();
				// TODO: verbose exception message
				try {
					final Field f = refc.getDeclaredField(name);
					f.setAccessible(true);
					if (f.getType() != type.returnType()) {
						throw new NoSuchFieldException();
					}
					return LOOKUP.unreflectGetter(f);
				} catch (final NoSuchFieldException | IllegalAccessException e) {
					if (!it.hasNext()) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}

	public static <T extends Throwable> RuntimeException rethrow(final Throwable t) throws T {
		//noinspection unchecked
		throw (T) t;
	}
}
