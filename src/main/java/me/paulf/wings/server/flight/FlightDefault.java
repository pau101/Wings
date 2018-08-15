package me.paulf.wings.server.flight;

import com.google.common.collect.Lists;
import me.paulf.wings.server.capability.Flight;
import me.paulf.wings.server.flight.state.State;
import me.paulf.wings.server.flight.state.StateIdle;
import me.paulf.wings.server.item.ItemWings;
import me.paulf.wings.util.CubicBezier;
import me.paulf.wings.util.function.FloatConsumer;
import me.paulf.wings.util.Mth;
import me.paulf.wings.util.SmoothingFunction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public final class FlightDefault implements Flight {
	private static final String IS_FLYING  = "isFlying";

	private static final String TIME_FLYING = "timeFlying";

	private static final CubicBezier FLY_AMOUNT_CURVE = new CubicBezier(0.37F, 0.13F, 0.3F, 1.12F);

	private static final int INITIAL_TIME_FLYING = 0;

	private static final int MAX_TIME_FLYING = 20;

	private static final float MIN_SPEED = 0.03F;

	private static final float MAX_SPEED = 0.0715F;

	private static final float Y_BOOST = 0.05F;

	private static final float FALL_REDUCTION = 0.9F;

	private static final float PITCH_OFFSET = 30.0F;

	private static final int DAMAGE_RATE = 60;

	private final List<FlyingListener> flyingListeners = Lists.newArrayList();

	private final List<SyncListener> syncListeners = Lists.newArrayList();

	private int prevTimeFlying = INITIAL_TIME_FLYING;

	private int timeFlying = INITIAL_TIME_FLYING;

	private int flightTime;

	private boolean isFlying;

	private State state = new StateIdle();

	private Animator animator = Animator.ABSENT;

	private final SmoothingFunction eyeHeightFunc = SmoothingFunction.create(t -> Mth.easeOutCirc(Mth.easeInOut(t)));

	@Override
	public void setIsFlying(boolean isFlying, PlayerSet players) {
		if (this.isFlying != isFlying) {
			this.isFlying = isFlying;
			flightTime = 0;
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

	@Override
	public Vec3d getWingRotation(int index, float delta) {
		return animator.getWingRotation(index, delta);
	}

	@Override
	public Vec3d getFeatherRotation(int index, float delta) {
		return animator.getFeatherRotation(index, delta);
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
		return ItemWings.isUsable(ItemWings.get(player));
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
		if (player.world.isRemote) {
			double dx = player.posX - player.prevPosX;
			double dy = player.posY - player.prevPosY;
			double dz = player.posZ - player.prevPosZ;
			animator = ItemWings.getType(wings).getAnimator(animator);
			animator.update(dx, dy, dz);
			State state = this.state.update(isFlying(), dx, dy, dz, player);
			if (!this.state.equals(state)) {
				state.beginAnimation(animator);
			}
			this.state = state;
		} else if (isFlying()) {
			if (flightTime % DAMAGE_RATE == (DAMAGE_RATE - 1)) {
				wings.damageItem(1, player);
				if (!ItemWings.isUsable(wings)) {
					setIsFlying(false, PlayerSet.ofAll());
				}
			}
		}
		flightTime++;
	}

	@Override
	public void onUpdate(EntityPlayer player) {
		ItemStack wings = ItemWings.get(player);
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
	public void onUpdateEyeHeight(float value, float delta, FloatConsumer valueOut) {
		eyeHeightFunc.accept(getFlyingAmount(delta), SmoothingFunction.Sign.valueOf(isFlying()), value, valueOut);
	}

	@Override
	public void clone(Flight other, PlayerSet players) {
		setIsFlying(other.isFlying());
		setTimeFlying(other.getTimeFlying());
		sync(players);
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

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean(IS_FLYING, isFlying());
		compound.setInteger(TIME_FLYING, getTimeFlying());
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		setIsFlying(compound.getBoolean(IS_FLYING));
		setTimeFlying(compound.getInteger(TIME_FLYING));
	}
}
