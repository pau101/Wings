package me.paulf.wings.client.flight;

import me.paulf.wings.util.function.FloatConsumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public interface FlightView {
	void ifFormPresent(final Consumer<FormRenderer> consumer);

	void tick(final EntityPlayer player, final ItemStack wings);

	void tickEyeHeight(final float value, final float delta, final FloatConsumer valueOut);

	interface FormRenderer {
		ResourceLocation getTexture();

		void render(final float delta, final float scale);
	}
}
