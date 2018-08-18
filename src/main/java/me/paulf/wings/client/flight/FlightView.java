package me.paulf.wings.client.flight;

import me.paulf.wings.util.function.FloatConsumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public interface FlightView {
	Vec3d getWingRotation(int index, float delta);

	Vec3d getFeatherRotation(int index, float delta);

	void onUpdate(EntityPlayer player, ItemStack wings);

	void onUpdateEyeHeight(float value, float delta, FloatConsumer valueOut);
}
