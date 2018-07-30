package com.pau101.wings.server.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Consumer;

public interface Flight extends INBTSerializable<NBTTagCompound> {
	default void setIsFlying(boolean isFlying) {
		setIsFlying(isFlying, PlayerSet.empty());
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

	void onWornUpdate(EntityPlayer player);

	void onUpdate(EntityPlayer player);

	void onUpdateEyeHeight(float height, float delta, FloatConsumer heightOut);

	void clone(Flight other, PlayerSet players);

	void sync(PlayerSet players);

	void serialize(PacketBuffer buf);

	void deserialize(PacketBuffer buf);

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

	interface PlayerSet {
		void notify(Notifier notifier);

		static PlayerSet empty() {
			return n -> {};
		}

		static PlayerSet ofSelf() {
			return Notifier::notifySelf;
		}

		static PlayerSet ofPlayer(EntityPlayerMP player) {
			return n -> n.notifyPlayer(player);
		}

		static PlayerSet ofOthers() {
			return Notifier::notifyOthers;
		}

		static PlayerSet ofAll() {
			return n -> {
				n.notifySelf();
				n.notifyOthers();
			};
		}
	}

	interface Notifier {
		void notifySelf();

		void notifyPlayer(EntityPlayerMP player);

		void notifyOthers();

		static Notifier of(Runnable notifySelf, Consumer<EntityPlayerMP> notifyPlayer, Runnable notifyOthers) {
			return new Notifier() {
				@Override
				public void notifySelf() {
					notifySelf.run();
				}

				@Override
				public void notifyPlayer(EntityPlayerMP player) {
					notifyPlayer.accept(player);
				}

				@Override
				public void notifyOthers() {
					notifyOthers.run();
				}
			};
		}
	}

	@FunctionalInterface
	interface FloatConsumer {
		void accept(float value);
	}
}
