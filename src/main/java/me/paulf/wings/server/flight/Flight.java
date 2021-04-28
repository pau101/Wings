package me.paulf.wings.server.flight;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Consumer;

public interface Flight {
	default void setIsFlying(final boolean isFlying) {
		this.setIsFlying(isFlying, PlayerSet.empty());
	}

	void setIsFlying(final boolean isFlying, final PlayerSet players);

	boolean isFlying();

	default void toggleIsFlying(final PlayerSet players) {
		this.setIsFlying(!this.isFlying(), players);
	}

	void setTimeFlying(final int timeFlying);

	int getTimeFlying();

	void setWing(ResourceLocation wing);

	ResourceLocation getWing();

	float getFlyingAmount(final float delta);

	void registerFlyingListener(final FlyingListener listener);

	void registerSyncListener(final SyncListener listener);

	boolean canFly(final PlayerEntity player);

	boolean canLand(final PlayerEntity player);

	void tick(final PlayerEntity player);

	void onFlown(final PlayerEntity player, final Vector3d direction);

	void clone(final Flight other);

	void sync(final PlayerSet players);

	void serialize(final PacketBuffer buf);

	void deserialize(final PacketBuffer buf);

	interface FlyingListener {
		void onChange(final boolean isFlying);

		static Consumer<FlyingListener> onChangeUsing(final boolean isFlying) {
			return l -> l.onChange(isFlying);
		}
	}

	interface SyncListener {
		void onSync(final PlayerSet players);

		static Consumer<SyncListener> onSyncUsing(final PlayerSet players) {
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

		static PlayerSet ofPlayer(final ServerPlayerEntity player) {
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

		void notifyPlayer(ServerPlayerEntity player);

		void notifyOthers();

		static Notifier of(final Runnable notifySelf, final Consumer<ServerPlayerEntity> notifyPlayer, final Runnable notifyOthers) {
			return new Notifier() {
				@Override
				public void notifySelf() {
					notifySelf.run();
				}

				@Override
				public void notifyPlayer(final ServerPlayerEntity player) {
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
