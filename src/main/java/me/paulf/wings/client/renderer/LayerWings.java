package me.paulf.wings.client.renderer;

import com.google.common.collect.ImmutableMap;
import me.paulf.wings.client.model.ModelWings;
import me.paulf.wings.client.model.ModelWingsAvian;
import me.paulf.wings.client.model.ModelWingsInsectoid;
import me.paulf.wings.server.flight.WingType;
import me.paulf.wings.server.item.ItemWings;
import me.paulf.wings.server.item.StandardWing;
import me.paulf.wings.util.FloatConsumer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;

public final class LayerWings implements LayerRenderer<AbstractClientPlayer> {
	private final RenderPlayer renderer;

	private final ModelWings avian = new ModelWingsAvian();

	private final ModelWings insectoid = new ModelWingsInsectoid();

	private final ImmutableMap<StandardWing, ModelWings> models = ImmutableMap.<StandardWing, ModelWings>builder()
		.put(StandardWing.ANGEL, avian)
		.put(StandardWing.SLIME, insectoid)
		.put(StandardWing.BLUE_BUTTERFLY, insectoid)
		.put(StandardWing.MONARCH_BUTTERFLY, insectoid)
		.put(StandardWing.FIRE, avian)
		.put(StandardWing.BAT, avian)
		.put(StandardWing.FAIRY, insectoid)
		.put(StandardWing.EVIL, avian)
		.put(StandardWing.DRAGON, avian)
		.build();

	private final TransformFunction transform;

	public LayerWings(RenderPlayer renderer, TransformFunction transform) {
		this.renderer = renderer;
		this.transform = transform;
	}

	@Override
	public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float delta, float age, float yawHead, float headPitch, float scale) {
		ItemStack stack;
		if (!player.isInvisible() && !(stack = ItemWings.get(player)).isEmpty()) {
			WingType type = ItemWings.getType(stack);
			ModelWings model = models.getOrDefault(type, ModelWings.NONE);
			renderer.bindTexture(type.getTexture());
			GlStateManager.pushMatrix();
			transform.apply(player, scale, renderer.getMainModel().bipedBody::postRender);
			GlStateManager.enableCull();
			model.render(player, limbSwing, limbSwingAmount, delta, yawHead, headPitch, scale);
			if (stack.hasEffect()) {
				LayerArmorBase.renderEnchantedGlint(renderer, player, model, limbSwing, limbSwingAmount, delta, delta, yawHead, headPitch, scale);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}
			GlStateManager.disableCull();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

	public interface TransformFunction {
		void apply(AbstractClientPlayer player, float scale, FloatConsumer bodyTransform);
	}
}
