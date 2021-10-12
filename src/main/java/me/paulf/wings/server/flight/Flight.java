package me.paulf.wings.server.flight;

import me.paulf.wings.server.apparatus.FlightApparatus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;

import java.util.function.Consumer;

public interface Flight {
    default void setIsFlying(boolean isFlying) {
        this.setIsFlying(isFlying, PlayerSet.empty());
    }

    void setIsFlying(boolean isFlying, PlayerSet players);

    boolean isFlying();

    default void toggleIsFlying(PlayerSet players) {
        this.setIsFlying(!this.isFlying(), players);
    }

    void setTimeFlying(int timeFlying);

    int getTimeFlying();

    default void setWing(FlightApparatus wing) {
        this.setWing(wing, PlayerSet.empty());
    }

    void setWing(FlightApparatus wing, PlayerSet players);

    FlightApparatus getWing();

    float getFlyingAmount(float delta);

    void registerFlyingListener(FlyingListener listener);

    void registerSyncListener(SyncListener listener);

    boolean canFly(PlayerEntity player);

    boolean canLand(PlayerEntity player);

    void tick(PlayerEntity player);

    void onFlown(PlayerEntity player, Vector3d direction);

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
            return n -> {
            };
        }

        static PlayerSet ofSelf() {
            return Notifier::notifySelf;
        }

        static PlayerSet ofPlayer(ServerPlayerEntity player) {
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

        static Notifier of(Runnable notifySelf, Consumer<ServerPlayerEntity> notifyPlayer, Runnable notifyOthers) {
            return new Notifier() {
                @Override
                public void notifySelf() {
                    notifySelf.run();
                }

                @Override
                public void notifyPlayer(ServerPlayerEntity player) {
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
