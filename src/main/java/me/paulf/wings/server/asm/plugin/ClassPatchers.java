package me.paulf.wings.server.asm.plugin;

import net.ilexiconn.llibrary.server.asm.ClassPatcher;
import net.ilexiconn.llibrary.server.asm.Descriptors;
import net.ilexiconn.llibrary.server.asm.MappingHandler;
import net.ilexiconn.llibrary.server.asm.MethodPatcher;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.Map;

public final class ClassPatchers {
	private ClassPatchers() {}

	public static MethodPatcher patchMethod(final ClassPatcher instance, final Object obj, String method, final Object... params) {
		final String desc = MappingHandler.INSTANCE.getClassMapping(Descriptors.method(params));
		method = MappingHandler.INSTANCE.getMethodMapping(obj, method, desc) + desc;
		final String cls = ReflectionHelper.getPrivateValue(ClassPatcher.class, instance, "cls");
		final MethodPatcher patcher = new MethodPatcher(instance, cls, method);
		final Map<String, MethodPatcher> patcherMap = ReflectionHelper.getPrivateValue(ClassPatcher.class, instance, "patcherMap");
		patcherMap.put(method, patcher);
		return patcher;
	}
}
