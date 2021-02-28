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

	public WingsSound(final PlayerEntity player, final Flight flight) {
		this(player, flight, true, 0, Math.nextAfter(0.0F, 1.0D));
	}

	private WingsSound(final PlayerEntity player, final Flight flight, final boolean repeat, final int repeatDelay, final float volume) {
		super(WingsSounds.ITEM_WINGS_FLYING.get(), SoundCategory.PLAYERS);
		this.player = player;
		this.flight = flight;
		this.repeat = repeat;
		this.repeatDelay = repeatDelay;
		this.volume = volume;
	}

	@Override
	public void tick() {
		if (!this.player.isAlive()) {
			this.finishPlaying();
		} else if (this.flight.getFlyingAmount(1.0F) > 0.0F) {
			this.x = (float) this.player.getPosX();
			this.y = (float) this.player.getPosY();
			this.z = (float) this.player.getPosZ();
			final float velocity = (float) this.player.getMotion().length();
			if (velocity >= 0.01F) {
				final float halfVel = velocity * 0.5F;
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
