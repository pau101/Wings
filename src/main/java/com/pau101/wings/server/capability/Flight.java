package com.pau101.wings.server.capability;

import java.util.function.Consumer;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface Flight extends ICapabilitySerializable<NBTTagCompound> {
	default void setIsFlying(boolean isFlying) {
		setIsFlying(isFlying, PlayerSet.NONE);
	}

	void setIsFlying(boolean isFlying, PlayerSet players);

	boolean isFlying();

	default void toggleIsFlying(PlayerSet players) {
		setIsFlying(!isFlying(), players);
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

	void clone(Flight other, PlayerSet players);

	void sync(PlayerSet players);

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
		void onSync(PlayerSet players);

		static Consumer<SyncListener> onSyncUsing(PlayerSet players) {
			return l -> l.onSync(players);
		}
	}

	enum PlayerSet {
		NONE {
			@Override
			public boolean includes(PlayerTarget player) {
				return false;
			}
		},
		OTHERS {
			@Override
			public boolean includes(PlayerTarget player) {
				return player == PlayerTarget.OTHERS;
			}
		},
		ALL {
			@Override
			public boolean includes(PlayerTarget player) {
				return true;
			}
		};

		public abstract boolean includes(PlayerTarget player);
	}

	enum PlayerTarget {
		SELF,
		OTHERS
	}
}
