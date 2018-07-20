package com.pau101.wings.server.flight;

import java.util.List;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.Lists;
import com.pau101.wings.server.capability.Flight;
import com.pau101.wings.server.flight.state.State;
import com.pau101.wings.server.flight.state.StateIdle;
import com.pau101.wings.server.item.StandardWing;
import com.pau101.wings.server.item.WingsItems;
import com.pau101.wings.util.CubicBezier;
import com.pau101.wings.util.Mth;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

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

	private static final float PITCH_OFFSET = 30;

	private static final int CHECK_FLIGHT_ABILITY_RATE = 640;

	private final List<FlyingListener> flyingListeners = Lists.newArrayList();

	private final List<SyncListener> syncListeners = Lists.newArrayList();

	private int prevTimeFlying = INITIAL_TIME_FLYING;

	private int timeFlying = INITIAL_TIME_FLYING;

	private boolean isFlying;

	private State state = new StateIdle();

	private Animator animator = Animator.ABSENT;

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
		return getWingType(player).canFly();
	}

	private WingType getWingType(EntityPlayer player) {
		IBaublesItemHandler inv = BaublesApi.getBaublesHandler(player);
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() == WingsItems.WINGS) {
				return StandardWing.fromMeta(stack);
			}
		}
		return WingType.ABSENT;
	}

	@Override
	public void onWornUpdate(EntityPlayer player) {
		boolean isClient = player.world.isRemote, isUser = player.isUser();
		if (isUser) {
			if (isFlying()) {
				float speed = (float) MathHelper.clampedLerp(MIN_SPEED, MAX_SPEED, player.moveForward);
				float elevationBoost = Mth.transform(Math.abs(player.rotationPitch), 45, 90, 1, 0);
				float pc = -MathHelper.cos(-Mth.toRadians(player.rotationPitch - PITCH_OFFSET * elevationBoost));
				float ps = MathHelper.sin(-Mth.toRadians(player.rotationPitch - PITCH_OFFSET * elevationBoost));
				float yc = MathHelper.cos(-Mth.toRadians(player.rotationYaw) - Mth.PI);
				float ys = MathHelper.sin(-Mth.toRadians(player.rotationYaw) - Mth.PI);
				player.motionX += ys * pc * speed;
				player.motionY += ps * speed + Y_BOOST * (player.rotationPitch > 0 ? elevationBoost : 1);
				player.motionZ += yc * pc * speed;
				if (getTimeFlying() == MAX_TIME_FLYING && player.onGround) {
					setIsFlying(false, PlayerSet.ofOthers());
				}
			}
			if (player.motionY < 0) {
				player.motionY *= FALL_REDUCTION;
			}
		}
		if (isClient) {
			double dx = player.posX - player.prevPosX;
			double dy = player.posY - player.prevPosY;
			double dz = player.posZ - player.prevPosZ;
			animator = getWingType(player).getAnimator(animator);
			animator.update(dx, dy, dz);
			State state = this.state.update(isFlying(), dx, dy, dz, player);
			if (!this.state.equals(state)) {
				state.beginAnimation(animator);
			}
			this.state = state;
		} else if (player.ticksExisted % CHECK_FLIGHT_ABILITY_RATE == 0 && !canFly(player)) {
			setIsFlying(false, PlayerSet.ofAll());
		}
		player.fallDistance = 0;
	}

	@Override
	public void onUpdate(EntityPlayer player) {
		setPrevTimeFlying(getTimeFlying());
		if (isFlying()) {
			if (getTimeFlying() < MAX_TIME_FLYING) {
				setTimeFlying(getTimeFlying() + 1);
			}
		} else {
			if (getTimeFlying() > INITIAL_TIME_FLYING) {
				setTimeFlying(getTimeFlying() - 1);
			}
		}
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
