package me.paulf.wings.client.renderer;

import com.google.common.collect.ImmutableMap;
import me.paulf.wings.client.model.ModelWings;
import me.paulf.wings.client.model.ModelWingsAvian;
import me.paulf.wings.client.model.ModelWingsInsectoid;
import me.paulf.wings.server.flight.WingType;
import me.paulf.wings.server.item.ItemWings;
import me.paulf.wings.server.item.StandardWing;
import me.paulf.wings.util.Mth;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public final class LayerWings implements LayerRenderer<AbstractClientPlayer> {
	private final RenderLivingBase<?> renderer;

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

	public LayerWings(RenderLivingBase<?> renderer) {
		this.renderer = renderer;
	}

	@Override
	public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float delta, float age, float yawHead, float headPitch, float scale) {
		ItemStack stack;
		if (!player.isInvisible() && !(stack = ItemWings.get(player)).isEmpty()) {
			WingType type = ItemWings.getType(stack);
			renderer.bindTexture(type.getTexture());
			GlStateManager.pushMatrix();
			float swing = player.getSwingProgress(delta);
			if (swing > 0.0F) {
				float theta = Mth.toDegrees(MathHelper.sin(MathHelper.sqrt(swing) * Mth.TAU) * 0.2F);
				GlStateManager.rotate(theta, 0.0F, 1.0F, 0.0F);
			}
			if (player.isSneaking()) {
				GlStateManager.translate(0F, 0.2F, 0F);
				GlStateManager.rotate(Mth.toDegrees(0.5F), 1.0F, 0.0F, 0.0F);
			}
			ModelWings model = models.getOrDefault(type, ModelWings.NONE);
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
}
