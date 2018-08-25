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

	private WingState state = voidState;

	@Override
	public void setIsFlying(boolean isFlying, PlayerSet players) {
		if (this.isFlying != isFlying) {
			this.isFlying = isFlying;
			flyingListeners.forEach(FlyingListener.onChangeUsing(isFlying));
			sync(players);
		}
	}

	@Override
	public boolean isFlying() {
		return isFlying;
	}

	@Override
	public void setTimeFlying(int timeFlying) {
		this.timeFlying = timeFlying;
	}

	@Override
	public int getTimeFlying() {
		return timeFlying;
	}

	@Override
	public float getFlyingAmount(float delta) {
		return FLY_AMOUNT_CURVE.eval(Mth.lerp(getPrevTimeFlying(), getTimeFlying(), delta) / MAX_TIME_FLYING);
	}

	private void setPrevTimeFlying(int prevTimeFlying) {
		this.prevTimeFlying = prevTimeFlying;
	}

	private int getPrevTimeFlying() {
		return prevTimeFlying;
	}

	@Override
	public void registerFlyingListener(FlyingListener listener) {
		flyingListeners.add(listener);
	}

	@Override
	public void registerSyncListener(SyncListener listener) {
		syncListeners.add(listener);
	}

	@Override
	public boolean canFly(EntityPlayer player) {
		ItemStack stack = FlightApparatuses.find(player);
		FlightApparatus apparatus = FlightApparatuses.get(stack);
		return apparatus != null && apparatus.isUsable(stack);
	}

	private void onWornUpdate(EntityPlayer player, ItemStack wings) {
		if (player.isServerWorld()) {
			if (isFlying()) {
				float speed = (float) MathHelper.clampedLerp(MIN_SPEED, MAX_SPEED, player.moveForward);
				float elevationBoost = Mth.transform(
					Math.abs(player.rotationPitch),
					45.0F, 90.0F,
					1.0F, 0.0F
				);
				float pitch = -Mth.toRadians(player.rotationPitch - PITCH_OFFSET * elevationBoost);
				float yaw = -Mth.toRadians(player.rotationYaw) - Mth.PI;
				float vxz = -MathHelper.cos(pitch);
				float vy = MathHelper.sin(pitch);
				float vz = MathHelper.cos(yaw);
				float vx = MathHelper.sin(yaw);
				player.motionX += vx * vxz * speed;
				player.motionY += vy * speed + Y_BOOST * (player.rotationPitch > 0.0F ? elevationBoost : 1.0D);
				player.motionZ += vz * vxz * speed;
			}
			if (player.motionY < 0.0D) {
				player.motionY *= FALL_REDUCTION;
			}
			player.fallDistance = 0.0F;
		}
		if (!player.world.isRemote) {
			FlightApparatus apparatus = FlightApparatuses.get(wings);
			if (apparatus == null) {
				state = state.next();
			} else if (apparatus.isUsable(wings)) {
				(state = state.next(wings, apparatus)).onUpdate(player, wings);
			} else if (isFlying()) {
				setIsFlying(false, PlayerSet.ofAll());
				state = state.next();
			}
		}
	}

	@Override
	public void onUpdate(EntityPlayer player, ItemStack wings) {
		if (!wings.isEmpty()) {
			onWornUpdate(player, wings);
		} else if (!player.world.isRemote && isFlying()) {
			setIsFlying(false, Flight.PlayerSet.ofAll());
		}
		setPrevTimeFlying(getTimeFlying());
		if (isFlying()) {
			if (getTimeFlying() < MAX_TIME_FLYING) {
				setTimeFlying(getTimeFlying() + 1);
			} else if (player.isUser() && player.onGround) {
				setIsFlying(false, PlayerSet.ofOthers());
			}
		} else {
			if (getTimeFlying() > INITIAL_TIME_FLYING) {
				setTimeFlying(getTimeFlying() - 1);
			}
		}
	}

	@Override
	public void clone(Flight other) {
		setIsFlying(other.isFlying());
		setTimeFlying(other.getTimeFlying());
	}

	@Override
	public void sync(PlayerSet players) {
		syncListeners.forEach(SyncListener.onSyncUsing(players));
	}

	@Override
	public void serialize(PacketBuffer buf) {
		buf.writeBoolean(isFlying());
		buf.writeVarInt(getTimeFlying());
	}

	@Override
	public void deserialize(PacketBuffer buf) {
		setIsFlying(buf.readBoolean());
		setTimeFlying(buf.readVarInt());
	}

	public static final class Serializer implements NBTSerializer<FlightDefault, NBTTagCompound> {
		private static final String IS_FLYING  = "isFlying";

		private static final String TIME_FLYING = "timeFlying";

		private final Supplier<FlightDefault> factory;

		public Serializer(Supplier<FlightDefault> factory) {
			this.factory = factory;
		}

		@Override
		public NBTTagCompound serialize(FlightDefault instance) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setBoolean(IS_FLYING, instance.isFlying());
			compound.setInteger(TIME_FLYING, instance.getTimeFlying());
			return compound;
		}

		@Override
		public FlightDefault deserialize(NBTTagCompound compound) {
			FlightDefault f = factory.get();
			f.setIsFlying(compound.getBoolean(IS_FLYING));
			f.setTimeFlying(compound.getInteger(TIME_FLYING));
			return f;
		}
	}

	private final class WingState {
		private final Item item;

		private final FlightApparatus.FlightState activity;

		private WingState(Item item, FlightApparatus.FlightState activity) {
			this.item = item;
			this.activity = activity;
		}

		private WingState next() {
			return voidState;
		}

		private WingState next(ItemStack stack, FlightApparatus wf) {
			Item item = stack.getItem();
			if (this.item.equals(item)) {
				return this;
			}
			return new WingState(item, wf.createState(FlightDefault.this));
		}

		private void onUpdate(EntityPlayer player, ItemStack stack) {
			activity.onUpdate(player, stack);
		}
	}
}
