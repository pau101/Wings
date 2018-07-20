package com.pau101.wings.client.renderer;

import baubles.api.render.IRenderBauble;
import com.google.common.collect.ImmutableMap;
import com.pau101.wings.client.model.ModelWings;
import com.pau101.wings.client.model.ModelWingsAvian;
import com.pau101.wings.client.model.ModelWingsInsectoid;
import com.pau101.wings.server.capability.FlightCapability;
import com.pau101.wings.server.item.StandardWing;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public final class WingsRenderer {
	private final ModelWings AVIAN = new ModelWingsAvian();

	private final ModelWings INSECTOID = new ModelWingsInsectoid();

	private final ImmutableMap<StandardWing, ModelWings> models = ImmutableMap.<StandardWing, ModelWings>builder()
		.put(StandardWing.ANGEL, AVIAN)
		.put(StandardWing.SLIME, INSECTOID)
		.put(StandardWing.BLUE_BUTTERFLY, INSECTOID)
		.put(StandardWing.MONARCH_BUTTERFLY, INSECTOID)
		.put(StandardWing.FIRE, AVIAN)
		.put(StandardWing.BAT, AVIAN)
		.put(StandardWing.FAIRY, INSECTOID)
		.put(StandardWing.EVIL, AVIAN)
		.put(StandardWing.DRAGON, AVIAN)
		.build();

	public void render(ItemStack stack, EntityPlayer player, IRenderBauble.RenderType renderType, float delta) {
		if (renderType == IRenderBauble.RenderType.BODY) {
			StandardWing type = StandardWing.fromMeta(stack);
			Minecraft.getMinecraft().getTextureManager().bindTexture(type.getTexture());
			IRenderBauble.Helper.rotateIfSneaking(player);
			models.getOrDefault(type, ModelWings.NONE).render(stack, player, FlightCapability.get(player), delta);
		}
	}
}
