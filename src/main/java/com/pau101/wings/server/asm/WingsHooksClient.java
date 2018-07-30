package com.pau101.wings.server.asm;

import com.pau101.wings.util.Access;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.lang.invoke.MethodHandle;

public final class WingsHooksClient {
	private WingsHooksClient() {}

	private static int selectedItemSlot = 0;

	public static boolean onCheckRenderEmptyHand(boolean isMainHand, ItemStack itemStackMainHand) {
		return isMainHand || !isMap(itemStackMainHand);
	}

	public static boolean onCheckDoReequipAnimation(ItemStack from, ItemStack to, int slot) {
		boolean fromEmpty = from.isEmpty(), toEmpty = to.isEmpty();
		boolean isOffhand = slot == -1;
		if (toEmpty && isOffhand) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayerSP player = mc.player;
			if (player == null) {
				return true;
			}
			boolean fromMap = isMap(GetItemStackMainHand.invoke(mc.getItemRenderer()));
			boolean toMap = isMap(player.getHeldItemMainhand());
			if (toMap != fromMap) {
				return true;
			}
			if (fromEmpty) {
				EmptyOffHandPresentEvent ev = new EmptyOffHandPresentEvent(player);
				MinecraftForge.EVENT_BUS.post(ev);
				return ev.getResult() != Event.Result.ALLOW;
			}
		}
		if (fromEmpty || toEmpty) {
			return fromEmpty != toEmpty;
		}
		boolean hasSlotChange = !isOffhand && selectedItemSlot != (selectedItemSlot = slot);
		return from.getItem().shouldCauseReequipAnimation(from, to, hasSlotChange);
	}

	private static boolean isMap(ItemStack stack) {
		return stack.getItem() instanceof ItemMap;
	}

	private static final class GetItemStackMainHand {
		private GetItemStackMainHand() {}

		private static final MethodHandle MH = Access.getter(ItemRenderer.class)
			.name("field_184831_bT ", "itemStackMainHand")
			.type(ItemStack.class);

		private static ItemStack invoke(ItemRenderer instance) {
			try {
				return (ItemStack) MH.invokeExact(instance);
			} catch (Throwable t) {
				throw Access.rethrow(t);
			}
		}
	}
}
