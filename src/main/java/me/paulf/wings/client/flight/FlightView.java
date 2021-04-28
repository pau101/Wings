package me.paulf.wings.client.flight;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.wings.util.function.FloatConsumer;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public interface FlightView {
	void ifFormPresent(final Consumer<FormRenderer> consumer);

	void tick();

	void tickEyeHeight(final float value, final FloatConsumer valueOut);

	interface FormRenderer {
		ResourceLocation getTexture();

		void render(final MatrixStack matrixStack, final IVertexBuilder buffer, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha, final float delta);
	}
}
