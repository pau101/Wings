package me.paulf.wings.server.asm;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ApplyPlayerRotationsEvent extends PlayerEvent {
    private final MatrixStack matrixStack;

    private final float delta;

    public ApplyPlayerRotationsEvent(final PlayerEntity player, final MatrixStack matrixStack, final float delta) {
        super(player);
        this.matrixStack = matrixStack;
        this.delta = delta;
    }

    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    public float getDelta() {
        return this.delta;
    }
}
