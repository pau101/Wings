package com.pau101.wings.server.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Flight extends ICapabilitySerializable<NBTTagCompound> {
	default void setIsFlying(boolean isFlying) {
		setIsFlying(isFlying, PlayerSet.NONE);
	}

	void setIsFlying(boolean isFlying, PlayerScope notificationScope);

	boolean isFlying();

	default void toggleIsFlying(PlayerScope notificationScope) {
		setIsFlying(!isFlying(), notificationScope);
	}

	void setTimeFlying(int timeFlying);

	int getTimeFlying();

	float getFlyingAmount(float delta);

	Vec3d getWingRotation(int index, float delta);

	Vec3d getFeatherRotation(int index, float delta);

	void registerFlyingListener(FlyingListener listener);

	void registerSyncListener(SyncListener listener);

	boolean canFly(EntityPlayer player);

	void update(EntityPlayer player);

	void clone(Flight other, PlayerScope notificationScope);

	void sync(PlayerSet toPlayers);

	void serialize(PacketBuffer buf);

	void deserialize(PacketBuffer buf);

	@Override
	default boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == FlightCapability.CAPABILITY;
	}

	@Nullable
	@Override
	default <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		return hasCapability(capability, facing) ? FlightCapability.CAPABILITY.cast(this) : null;
	}

	interface FlyingListener {
		void onChange(boolean isFlying);

		static Consumer<FlyingListener> onChangeUsing(boolean isFlying) {
			return l -> l.onChange(isFlying);
		}
	}

	interface SyncListener {
		void onSync(PlayerSet toPlayers);

		static Consumer<SyncListener> onSyncUsing(PlayerSet toPlayers) {
			return l -> l.onSync(toPlayers);
		}
	}

	interface PlayerScope {
		PlayerScope NONE = flight -> {};

		void send(Flight flight);
	}

	enum PlayerSet implements PlayerScope {
		OTHERS(p -> new EntityPlayer[] { p }),
		ALL(p -> null);

		private final Function<EntityPlayer, EntityPlayer[]> excluder;

		PlayerSet(Function<EntityPlayer, EntityPlayer[]> excluder) {
			this.excluder = excluder;
		}

		@Nullable
		public final EntityPlayer[] getExclusions(EntityPlayer owner) {
			return excluder.apply(owner);
		}

		@Override
		public final void send(Flight flight) {
			flight.sync(this);
		}
	}

	enum AnimationType {
		FALL, GLIDE, IDLE, LIFT;
	}
}
