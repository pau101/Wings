package me.paulf.wings.server.asm;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.paulf.wings.util.Access;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MapItem;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.lang.invoke.MethodHandle;

public final class WingsHooksClient {
	private WingsHooksClient() {}

	private static int selectedItemSlot = 0;

	public static void onSetPlayerRotationAngles(final LivingEntity living, final PlayerModel<?> model, final float ageTicks, final float pitch) {
		if (living instanceof PlayerEntity) {
			MinecraftForge.EVENT_BUS.post(new AnimatePlayerModelEvent((PlayerEntity) living, model, ageTicks, pitch));
		}
	}

	public static void onApplyPlayerRotations(final AbstractClientPlayerEntity player, final MatrixStack matrixStack, final float delta) {
		MinecraftForge.EVENT_BUS.post(new ApplyPlayerRotationsEvent(player, matrixStack, delta));
	}

	public static void onTurn(final Entity entity, final float deltaYaw) {
		if (entity instanceof LivingEntity) {
			final LivingEntity living = (LivingEntity) entity;
			final float theta = MathHelper.wrapDegrees(living.rotationYaw - living.renderYawOffset);
			final GetLivingHeadLimitEvent ev = GetLivingHeadLimitEvent.create(living);
			MinecraftForge.EVENT_BUS.post(ev);
			final float limit = ev.getHardLimit();
			if (theta < -limit || theta > limit) {
				living.renderYawOffset += deltaYaw;
				living.prevRenderYawOffset += deltaYaw;
			}
		}
	}

	public static boolean onCheckRenderEmptyHand(final boolean isMainHand, final ItemStack itemStackMainHand) {
		return isMainHand || !isMap(itemStackMainHand);
	}

	public static boolean onCheckDoReequipAnimation(final ItemStack from, final ItemStack to, final int slot) {
		final boolean fromEmpty = from.isEmpty();
		final boolean toEmpty = to.isEmpty();
		final boolean isOffHand = slot == -1;
		if (toEmpty && isOffHand) {
			final Minecraft mc = Minecraft.getInstance();
			final ClientPlayerEntity player = mc.player;
			if (player == null) {
				return true;
			}
			final boolean fromMap = isMap(GetItemStackMainHand.invoke(mc.getFirstPersonRenderer()));
			final boolean toMap = isMap(player.getHeldItemMainhand());
			if (fromMap || toMap) {
				return fromMap != toMap;
			}
			if (fromEmpty) {
				final EmptyOffHandPresentEvent ev = new EmptyOffHandPresentEvent(player);
				MinecraftForge.EVENT_BUS.post(ev);
				return ev.getResult() != Event.Result.ALLOW;
			}
		}
		if (fromEmpty || toEmpty) {
			return fromEmpty != toEmpty;
		}
		final boolean hasSlotChange = !isOffHand && selectedItemSlot != (selectedItemSlot = slot);
		return from.getItem().shouldCauseReequipAnimation(from, to, hasSlotChange);
	}

	private static boolean isMap(final ItemStack stack) {
		return stack.getItem() instanceof MapItem;
	}

	private static final class GetItemStackMainHand {
		private GetItemStackMainHand() {}

		private static final MethodHandle MH = Access.getter(FirstPersonRenderer.class)
			.name("field_187467_d", "itemStackMainHand")
			.type(ItemStack.class);

		private static ItemStack invoke(final FirstPersonRenderer instance) {
			try {
				return (ItemStack) MH.invokeExact(instance);
			} catch (final Throwable t) {
				throw Access.rethrow(t);
			}
		}
	}
}
