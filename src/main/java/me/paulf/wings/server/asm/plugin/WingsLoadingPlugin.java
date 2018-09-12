package me.paulf.wings.server.asm.plugin;

import me.paulf.wings.WingsMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ModContainerFactory;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name(WingsMod.ID)
@IFMLLoadingPlugin.MCVersion(MinecraftForge.MC_VERSION)
@IFMLLoadingPlugin.SortingIndex(1002)
@IFMLLoadingPlugin.TransformerExclusions("me.paulf.wings.server.asm.plugin.")
public final class WingsLoadingPlugin implements IFMLLoadingPlugin {
	public WingsLoadingPlugin() {}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {
			"me.paulf.wings.server.asm.WingsRuntimePatcher",
			"me.paulf.wings.server.asm.mobends.WingsMoBendsRuntimePatcher"
		};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Nullable
	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		ModContainerFactory.instance().registerContainerType(Type.getType(Integration.class), IntegrationContainer.class);
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
