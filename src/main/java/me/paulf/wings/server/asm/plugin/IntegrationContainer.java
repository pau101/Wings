package me.paulf.wings.server.asm.plugin;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import java.util.Map;

public class IntegrationContainer extends FMLModContainer {
	public IntegrationContainer(final String className, final ModCandidate candidate, final Map<String, Object> descriptor) {
		super(className, candidate, createDescriptor(descriptor));
	}

	@Override
	public boolean registerBus(final EventBus bus, final LoadController controller) {
		final Map<String, ModContainer> mods = Loader.instance().getIndexedModList();
		for (final ArtifactVersion requirement : this.getDependencies()) {
			final ModContainer mod = mods.get(requirement.getLabel());
			if (mod == null || !requirement.containsVersion(mod.getProcessedVersion())) {
				this.setEnabledState(false);
				break;
			}
		}
		return super.registerBus(bus, controller);
	}

	private static Map<String, Object> createDescriptor(final Map<String, Object> descriptor) {
		final Object id = descriptor.get("id");
		final Object name = descriptor.get("name");
		final Object condition = descriptor.get("condition");
		return ImmutableMap.<String, Object>builder()
			.put("modid", id)
			.put("name", name)
			.put("version", "1.0.0")
			.put("dependencies", condition)
			.build();
	}
}
