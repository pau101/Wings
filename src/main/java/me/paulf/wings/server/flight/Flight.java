package me.paulf.wings.server.flight;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;

import java.util.function.Consumer;

public interface Flight {
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

	void registerFlyingListener(FlyingListener listener);

	void registerSyncListener(SyncListener listener);

	boolean canFly(EntityPlayer player);

	boolean canLand(EntityPlayer player, ItemStack wings);

	void tick(EntityPlayer player, ItemStack wings);

	void onFlown(EntityPlayer player, ItemStack wings, Vec3d direction);

	void clone(Flight other);

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
}
