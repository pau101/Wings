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

	public WingsSound(EntityPlayer player, Flight flight) {
		this(player, flight, true, 0, Math.nextAfter(0.0F, 1.0D));
	}

	private WingsSound(EntityPlayer player, Flight flight, boolean repeat, int repeatDelay, float volume) {
		super(WingsSounds.ITEM_WINGS_FLYING, SoundCategory.PLAYERS);
		this.player = player;
		this.flight = flight;
		this.repeat = repeat;
		this.repeatDelay = repeatDelay;
		this.volume = volume;
	}

	@Override
	public void update() {
		if (player.isDead) {
			donePlaying = true;
		} else if (flight.getFlyingAmount(1.0F) > 0.0F) {
			xPosF = (float) player.posX;
			yPosF = (float) player.posY;
			zPosF = (float) player.posZ;
			float velocity = MathHelper.sqrt(
			player.motionX * player.motionX +
				player.motionZ * player.motionZ +
				player.motionY * player.motionY
			);
			if (velocity >= 0.01F) {
				float halfVel = velocity * 0.5F;
				volume = MathHelper.clamp(halfVel * halfVel, 0.0F, 1.0F);
			} else {
				volume = 0.0F;
			}
			final float cutoff = 0.8F;
			if (volume > cutoff) {
				pitch = 1.0F + (volume - cutoff);
			} else {
				pitch = 1.0F;
			}
		}
	}
}
