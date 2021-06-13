package me.paulf.wings.server.apparatus;

import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.item.WingSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Objects;

public final class SimpleFlightApparatus implements FlightApparatus {
    private final WingSettings settings;

    public SimpleFlightApparatus(WingSettings settings) {
        this.settings = Objects.requireNonNull(settings);
    }

    @Override
    public void onFlight(PlayerEntity player, Vector3d direction) {
        int distance = Math.round((float) direction.length() * 100.0F);
        if (distance > 0) {
            player.causeFoodExhaustion(distance * this.settings.getFlyingExertion());
        }
    }

    @Override
    public void onLanding(PlayerEntity player, Vector3d direction) {
        player.causeFoodExhaustion(this.settings.getLandingExertion());
    }

    @Override
    public boolean isUsable(PlayerEntity player) {
        return player.getFoodData().getFoodLevel() >= this.settings.getRequiredFlightSatiation();
    }

    @Override
    public boolean isLandable(PlayerEntity player) {
        return player.getFoodData().getFoodLevel() >= this.settings.getRequiredLandSatiation();
    }

    @Override
    public FlightState createState(Flight flight) {
        return (player) -> {
        };
    }
}
