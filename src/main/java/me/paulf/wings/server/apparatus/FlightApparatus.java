package me.paulf.wings.server.apparatus;

import me.paulf.wings.server.flight.Flight;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

public interface FlightApparatus {
    FlightApparatus NONE = new FlightApparatus() {
        @Override
        public void onFlight(PlayerEntity player, Vector3d direction) {
        }

        @Override
        public void onLanding(PlayerEntity player, Vector3d direction) {
        }

        @Override
        public boolean isUsable(PlayerEntity player) {
            return true;
        }

        @Override
        public boolean isLandable(PlayerEntity player) {
            return true;
        }

        @Override
        public FlightState createState(Flight flight) {
            return FlightState.NONE;
        }
    };

    void onFlight(PlayerEntity player, Vector3d direction);

    void onLanding(PlayerEntity player, Vector3d direction);

    boolean isUsable(PlayerEntity player);

    boolean isLandable(PlayerEntity player);

    FlightState createState(Flight flight);

    interface FlightState {
        FlightState NONE = (player) -> {
        };

        void onUpdate(PlayerEntity player);
    }
}
