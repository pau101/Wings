package me.paulf.wings.server.asm.plugin;

import net.ilexiconn.llibrary.server.asm.Descriptors;
import net.ilexiconn.llibrary.server.asm.InsnPredicate;
import net.ilexiconn.llibrary.server.asm.MappingHandler;
import net.ilexiconn.llibrary.server.asm.MethodPatcher;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

// FIXME: temporary until release for https://github.com/gegy1000/LLibraryCore/commit/e44ed25
public final class MethodExt extends InsnPredicate.Method {
	private final String owner;
	private final String desc;
	private final String name;

	public MethodExt(final Object owner, final String name, final Object... desc) {
		super("", "", "");
		this.owner = MappingHandler.INSTANCE.getClassMapping(owner);
		this.desc = MappingHandler.INSTANCE.getClassMapping(Descriptors.method(desc));
		this.name = MappingHandler.INSTANCE.getMethodMapping(owner, name, this.desc);
	}

	private MethodExt(final String owner, final String desc, final String name) {
		super("", "", "");
		this.owner = owner;
		this.desc = desc;
		this.name = name;
	}

	@Override
	public boolean test(final MethodPatcher.PredicateData predicateData) {
		return this.test(predicateData.node);
	}

	public boolean test(final AbstractInsnNode node) {
		if (node instanceof MethodInsnNode) {
			final MethodInsnNode mNode = (MethodInsnNode) node;
			return this.opcodePredicate.test(mNode.getOpcode()) &&
				this.owner.equals(mNode.owner) &&
				this.desc.equals(mNode.desc) &&
				this.name.equals(mNode.name);
		} else {
			return false;
		}
	}

	public MethodExt on(final Object target) {
		return new MethodExt(MappingHandler.INSTANCE.getClassMapping(target), this.desc, this.name);
	}
}
