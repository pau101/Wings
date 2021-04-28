package me.paulf.wings.server.effect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class WingedEffect extends Effect {
    protected WingedEffect(int color) {
        super(EffectType.BENEFICIAL, color);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }
}
