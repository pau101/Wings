package me.paulf.wings.server.flight;

import com.google.common.collect.Lists;
import me.paulf.wings.server.apparatus.FlightApparatus;
import me.paulf.wings.server.apparatus.FlightApparatuses;
import me.paulf.wings.util.CubicBezier;
import me.paulf.wings.util.Mth;
import me.paulf.wings.util.NBTSerializer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Supplier;

public final class FlightDefault implements Flight {
	private static final CubicBezier FLY_AMOUNT_CURVE = new CubicBezier(0.37F, 0.13F, 0.3F, 1.12F);

	private static final int INITIAL_TIME_FLYING = 0;

	private static final int MAX_TIME_FLYING = 20;

	private static final float MIN_SPEED = 0.03F;

	private static final float MAX_SPEED = 0.0715F;

	private static final float Y_BOOST = 0.05F;

	private static final float FALL_REDUCTION = 0.9F;

	private static final float PITCH_OFFSET = 30.0F;

	private final List<FlyingListener> flyingListeners = Lists.newArrayList();

	private final List<SyncListener> syncListeners = Lists.newArrayList();

	private final WingState voidState = new WingState(Items.AIR, FlightApparatus.FlightState.VOID);

	private int prevTimeFlying = INITIAL_TIME_FLYING;

	private int timeFlying = INITIAL_TIME_FLYING;

	private boolean isFlying;

	private WingState state = this.voidState;

	@Override
	public void setIsFlying(final boolean isFlying, final PlayerSet players) {
		if (this.isFlying != isFlying) {
			this.isFlying = isFlying;
			this.flyingListeners.forEach(FlyingListener.onChangeUsing(isFlying));
			this.sync(players);
		}
	}

	@Override
	public boolean isFlying() {
		return this.isFlying;
	}

	@Override
	public void setTimeFlying(final int timeFlying) {
		this.timeFlying = timeFlying;
	}

	@Override
	public int getTimeFlying() {
		return this.timeFlying;
	}

	@Override
	public float getFlyingAmount(final float delta) {
		return FLY_AMOUNT_CURVE.eval(Mth.lerp(this.getPrevTimeFlying(), this.getTimeFlying(), delta) / MAX_TIME_FLYING);
	}

	private void setPrevTimeFlying(final int prevTimeFlying) {
		this.prevTimeFlying = prevTimeFlying;
	}

	private int getPrevTimeFlying() {
		return this.prevTimeFlying;
	}

	@Override
	public void registerFlyingListener(final FlyingListener listener) {
		this.flyingListeners.add(listener);
	}

	@Override
	public void registerSyncListener(final SyncListener listener) {
		this.syncListeners.add(listener);
	}

	@Override
	public boolean canFly(final EntityPlayer player) {
		final ItemStack stack = FlightApparatuses.find(player);
		final FlightApparatus apparatus = FlightApparatuses.get(stack);
		return apparatus != null && apparatus.isUsable(player, stack);
	}

	@Override
	public boolean canLand(final EntityPlayer player, final ItemStack wings) {
		final FlightApparatus apparatus = FlightApparatuses.get(wings);
		return apparatus != null && apparatus.isLandable(player, wings);
	}

	private void onWornUpdate(final EntityPlayer player, final ItemStack wings) {
		if (player.isServerWorld()) {
			if (this.isFlying()) {
				final float speed = (float) MathHelper.clampedLerp(MIN_SPEED, MAX_SPEED, player.moveForward);
				final float elevationBoost = Mth.transform(
					Math.abs(player.rotationPitch),
					45.0F, 90.0F,
					1.0F, 0.0F
				);
				final float pitch = -Mth.toRadians(player.rotationPitch - PITCH_OFFSET * elevationBoost);
				final float yaw = -Mth.toRadians(player.rotationYaw) - Mth.PI;
				final float vxz = -MathHelper.cos(pitch);
				final float vy = MathHelper.sin(pitch);
				final float vz = MathHelper.cos(yaw);
				final float vx = MathHelper.sin(yaw);
				player.motionX += vx * vxz * speed;
				player.motionY += vy * speed + Y_BOOST * (player.rotationPitch > 0.0F ? elevationBoost : 1.0D);
				player.motionZ += vz * vxz * speed;
			}
			if (this.canLand(player, wings)) {
				if (player.motionY < 0.0D) {
					player.motionY *= FALL_REDUCTION;
				}
				player.fallDistance = 0.0F;
			}
		}
		if (!player.world.isRemote) {
			final FlightApparatus apparatus = FlightApparatuses.get(wings);
			if (apparatus == null) {
				this.state = this.state.next();
			} else if (apparatus.isUsable(player, wings)) {
				(this.state = this.state.next(wings, apparatus)).onUpdate(player, wings);
			} else if (this.isFlying()) {
				this.setIsFlying(false, PlayerSet.ofAll());
				this.state = this.state.next();
			}
		}
	}

	@Override
	public void tick(final EntityPlayer player, final ItemStack wings) {
		if (!wings.isEmpty()) {
			this.onWornUpdate(player, wings);
		} else if (!player.world.isRemote && this.isFlying()) {
			this.setIsFlying(false, Flight.PlayerSet.ofAll());
		}
		this.setPrevTimeFlying(this.getTimeFlying());
		if (this.isFlying()) {
			if (this.getTimeFlying() < MAX_TIME_FLYING) {
				this.setTimeFlying(this.getTimeFlying() + 1);
			} else if (player.isUser() && player.onGround) {
				this.setIsFlying(false, PlayerSet.ofOthers());
			}
		} else {
			if (this.getTimeFlying() > INITIAL_TIME_FLYING) {
				this.setTimeFlying(this.getTimeFlying() - 1);
			}
		}
	}

	@Override
	public void onFlown(final EntityPlayer player, final ItemStack wings, final Vec3d direction) {
		final FlightApparatus apparatus;
		if (!wings.isEmpty() && (apparatus = FlightApparatuses.get(wings)) != null) {
			if (this.isFlying()) {
				apparatus.onFlight(player, wings, direction);
			} else if (player.motionY < -0.5D) {
				apparatus.onLanding(player, wings, direction);
			}
		}
	}

	@Override
	public void clone(final Flight other) {
		this.setIsFlying(other.isFlying());
		this.setTimeFlying(other.getTimeFlying());
	}

	@Override
	public void sync(final PlayerSet players) {
		this.syncListeners.forEach(SyncListener.onSyncUsing(players));
	}

	@Override
	public void serialize(final PacketBuffer buf) {
		buf.writeBoolean(this.isFlying());
		buf.writeVarInt(this.getTimeFlying());
	}

	@Override
	public void deserialize(final PacketBuffer buf) {
		this.setIsFlying(buf.readBoolean());
		this.setTimeFlying(buf.readVarInt());
	}

	public static final class Serializer implements NBTSerializer<FlightDefault, NBTTagCompound> {
		private static final String IS_FLYING  = "isFlying";

		private static final String TIME_FLYING = "timeFlying";

		private final Supplier<FlightDefault> factory;

		public Serializer(final Supplier<FlightDefault> factory) {
			this.factory = factory;
		}

		@Override
		public NBTTagCompound serialize(final FlightDefault instance) {
			final NBTTagCompound compound = new NBTTagCompound();
			compound.setBoolean(IS_FLYING, instance.isFlying());
			compound.setInteger(TIME_FLYING, instance.getTimeFlying());
			return compound;
		}

		@Override
		public FlightDefault deserialize(final NBTTagCompound compound) {
			final FlightDefault f = this.factory.get();
			f.setIsFlying(compound.getBoolean(IS_FLYING));
			f.setTimeFlying(compound.getInteger(TIME_FLYING));
			return f;
		}
	}

	private final class WingState {
		private final Item item;

		private final FlightApparatus.FlightState activity;

		private WingState(final Item item, final FlightApparatus.FlightState activity) {
			this.item = item;
			this.activity = activity;
		}

		private WingState next() {
			return FlightDefault.this.voidState;
		}

		private WingState next(final ItemStack stack, final FlightApparatus wf) {
			final Item item = stack.getItem();
			if (this.item.equals(item)) {
				return this;
			}
			return new WingState(item, wf.createState(FlightDefault.this));
		}

		private void onUpdate(final EntityPlayer player, final ItemStack stack) {
			this.activity.onUpdate(player, stack);
		}
	}
}
