package me.paulf.wings.client.flight;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.paulf.wings.util.function.FloatConsumer;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public interface FlightView {
    void ifFormPresent(Consumer<FormRenderer> consumer);

    void tick();

    void tickEyeHeight(float value, FloatConsumer valueOut);

    interface FormRenderer {
        ResourceLocation getTexture();

        void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, float delta);
    }
}
