package me.paulf.wings.server.asm.mobends;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public final class WingsMoBendsHooks {
	private WingsMoBendsHooks() {}

	private static String name = "";

	public static boolean onTestPlayerAnimation(final EntityPlayer player, final boolean isElytraFlying) {
		if (isElytraFlying) {
			return true;
		}
		final GetMoBendsPlayerAnimationEvent ev = GetMoBendsPlayerAnimationEvent.create(player);
		MinecraftForge.EVENT_BUS.post(ev);
		return !(name = ev.get()).isEmpty();
	}

	public static String getPlayerAnimation() {
		return name.isEmpty() ? "elytra" : name;
	}
}
