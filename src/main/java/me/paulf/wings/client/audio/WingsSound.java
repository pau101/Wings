package me.paulf.wings.client.audio;

import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.sound.WingsSounds;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public final class WingsSound extends MovingSound {
	private final EntityPlayer player;

	private final Flight flight;

	public WingsSound(final EntityPlayer player, final Flight flight) {
		this(player, flight, true, 0, Math.nextAfter(0.0F, 1.0D));
	}

	private WingsSound(final EntityPlayer player, final Flight flight, final boolean repeat, final int repeatDelay, final float volume) {
		super(WingsSounds.ITEM_WINGS_FLYING, SoundCategory.PLAYERS);
		this.player = player;
		this.flight = flight;
		this.repeat = repeat;
		this.repeatDelay = repeatDelay;
		this.volume = volume;
	}

	@Override
	public void update() {
		if (this.player.isDead) {
			this.donePlaying = true;
		} else if (this.flight.getFlyingAmount(1.0F) > 0.0F) {
			this.xPosF = (float) this.player.posX;
			this.yPosF = (float) this.player.posY;
			this.zPosF = (float) this.player.posZ;
			final float velocity = MathHelper.sqrt(
				this.player.motionX * this.player.motionX +
				this.player.motionZ * this.player.motionZ +
				this.player.motionY * this.player.motionY
			);
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
