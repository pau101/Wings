package me.paulf.wings.client.flight;

import me.paulf.wings.util.function.FloatConsumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public interface FlightView {
	void ifFormPresent(Consumer<FormRenderer> consumer);

	void onUpdate(EntityPlayer player, ItemStack wings);

	void onUpdateEyeHeight(float value, float delta, FloatConsumer valueOut);

	interface FormRenderer {
		ResourceLocation getTexture();

		void render(float delta, float scale);
	}
}
