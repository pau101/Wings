package me.paulf.wings.client.audio;

import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.sound.WingsSounds;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public final class WingsSound extends TickableSound {
    private final PlayerEntity player;

    private final Flight flight;

    public WingsSound(PlayerEntity player, Flight flight) {
        this(player, flight, true, 0, Math.nextAfter(0.0F, 1.0D));
    }

    private WingsSound(PlayerEntity player, Flight flight, boolean repeat, int repeatDelay, float volume) {
        super(WingsSounds.ITEM_WINGS_FLYING.get(), SoundCategory.PLAYERS);
        this.player = player;
        this.flight = flight;
        this.looping = repeat;
        this.delay = repeatDelay;
        this.volume = volume;
    }

    @Override
    public void tick() {
        if (!this.player.isAlive()) {
            this.stop();
        } else if (this.flight.getFlyingAmount(1.0F) > 0.0F) {
            this.x = (float) this.player.getX();
            this.y = (float) this.player.getY();
            this.z = (float) this.player.getZ();
            float velocity = (float) this.player.getDeltaMovement().length();
            if (velocity >= 0.01F) {
                float halfVel = velocity * 0.5F;
                this.volume = MathHelper.clamp(halfVel * halfVel, 0.0F, 1.0F);
            } else {
                this.volume = 0.0F;
            }
            final float cutoff = 0.8F;
            if (this.volume > cutoff) {
                this.pitch = 1.0F + (this.volume - cutoff);
            } else {
                this.pitch = 1.0F;
            }
        } else {
            this.volume = 0.0F;
        }
    }
}
