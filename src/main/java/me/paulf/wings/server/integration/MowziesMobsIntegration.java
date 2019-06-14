package me.paulf.wings.server.integration;

import com.bobmowzie.mowziesmobs.client.model.entity.ModelWroughtnaut;
import com.bobmowzie.mowziesmobs.server.entity.wroughtnaut.EntityWroughtnaut;
import me.paulf.wings.client.renderer.LayerWings;
import me.paulf.wings.server.asm.plugin.Integration;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Integration(
	id = "mowzies_wings",
	name = "Mowzie's Wings",
	condition = "required-after:wings;after:mowziesmobs"
)
public final class MowziesMobsIntegration {
	@SidedProxy(modId = "mowzies_wings")
	private static Proxy proxy = new Proxy();

	@Mod.EventHandler
	public void init(final FMLPreInitializationEvent event) {
		proxy.preinit();
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent event) {
		proxy.init();
	}

	private static class Proxy {
		void preinit() {}

		void init() {}
	}

	public static final class ServerProxy extends Proxy {}

	public static final class ClientProxy extends Proxy {
		@Override
		void init() {
			super.init();
			final Render<?> render = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(EntityWroughtnaut.class);
			if (render instanceof RenderLivingBase<?>) {
				final RenderLivingBase<?> living = (RenderLivingBase<?>) render;
				final ModelBase model = living.getMainModel();
				if (model instanceof ModelWroughtnaut) {
					final AdvancedModelRenderer chest = ((ModelWroughtnaut) model).chest;
					living.addLayer(new LayerWings(living, (player, scale) -> {
						chest.parentedPostRender(scale);
						GlStateManager.translate(0.0F, 9.0F * scale, 18.0F * scale);
						GlStateManager.rotate(-35.0F, 1.0F, 0.0F, 0.0F);
					}));
				}
			}
		}
	}
}
