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
	public IntegrationContainer(String className, ModCandidate candidate, Map<String, Object> descriptor) {
		super(className, candidate, createDescriptor(descriptor));
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		Map<String, ModContainer> mods = Loader.instance().getIndexedModList();
		for (ArtifactVersion requirement : getDependencies()) {
			ModContainer mod = mods.get(requirement.getLabel());
			if (mod == null || !requirement.containsVersion(mod.getProcessedVersion())) {
				setEnabledState(false);
				break;
			}
		}
		return super.registerBus(bus, controller);
	}

	private static Map<String, Object> createDescriptor(Map<String, Object> descriptor) {
		Object id = descriptor.get("id");
		Object name = descriptor.get("name");
		Object condition = descriptor.get("condition");
		return ImmutableMap.<String, Object>builder()
			.put("modid", id)
			.put("name", name)
			.put("version", "1.0.0")
			.put("dependencies", condition)
			.build();
	}
}
