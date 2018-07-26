package com.pau101.wings.server.asm.plugin;

import net.ilexiconn.llibrary.server.asm.Descriptors;
import net.ilexiconn.llibrary.server.asm.InsnPredicate;
import net.ilexiconn.llibrary.server.asm.MappingHandler;
import net.ilexiconn.llibrary.server.asm.MethodPatcher;
import org.objectweb.asm.tree.MethodInsnNode;

// FIXME: temporary until release for https://github.com/gegy1000/LLibraryCore/commit/e44ed25
public final class MethodExt extends InsnPredicate.Method {
	private final String owner;
	private final String desc;
	private final String name;

	public MethodExt(Object owner, String name, Object... desc) {
		super("", "", "");
		this.owner = MappingHandler.INSTANCE.getClassMapping(owner);
		this.desc = MappingHandler.INSTANCE.getClassMapping(Descriptors.method(desc));
		this.name = MappingHandler.INSTANCE.getMethodMapping(owner, name, this.desc);
	}

	private MethodExt(String owner, String desc, String name) {
		super("", "", "");
		this.owner = owner;
		this.desc = desc;
		this.name = name;
	}

	@Override
	public boolean test(MethodPatcher.PredicateData predicateData) {
		if (predicateData.node instanceof MethodInsnNode) {
			MethodInsnNode node = (MethodInsnNode) predicateData.node;
			return this.opcodePredicate.test(predicateData.node.getOpcode()) && this.owner.equals(node.owner) && this.desc.equals(node.desc) && this.name.equals(node.name);
		} else {
			return false;
		}
	}

	public Method on(Object target) {
		return new MethodExt(MappingHandler.INSTANCE.getClassMapping(target), desc, name);
	}
}
