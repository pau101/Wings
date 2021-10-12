package me.paulf.wings.server.effect;

import me.paulf.wings.server.item.WingsItems;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

import java.util.ArrayList;
import java.util.List;

public class WingedEffect extends Effect {
    protected WingedEffect(int color) {
        super(EffectType.BENEFICIAL, color);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        List<ItemStack> ret = new ArrayList<>();
        ret.add(new ItemStack(WingsItems.BAT_BLOOD_BOTTLE.get()));
        return ret;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
