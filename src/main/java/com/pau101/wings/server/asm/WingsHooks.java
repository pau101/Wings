package com.pau101.wings.server.asm;

import java.lang.invoke.MethodHandle;

import com.pau101.wings.util.Access;
import com.pau101.wings.util.Mth;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.passive.EntityFlying;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public final class WingsHooks {
	private WingsHooks() {}

	public static boolean onFlightCheck(EntityPlayer player, boolean defaultValue) {
		PlayerFlightCheckEvent ev = new PlayerFlightCheckEvent(player);
		MinecraftForge.EVENT_BUS.post(ev);
		return ev.getResult() == Event.Result.ALLOW || ev.getResult() == Event.Result.DEFAULT && defaultValue;
	}

	public static float onGetCameraEyeHeight(Entity entity, float delta) {
		GetCameraEyeHeightEvent ev = GetCameraEyeHeightEvent.create(entity, delta);
		MinecraftForge.EVENT_BUS.post(ev);
		return ev.getValue();
	}

	public static void onUpdateBodyRotation(EntityLivingBase living, float movementYaw) {
		living.renderYawOffset += MathHelper.wrapDegrees(movementYaw - living.renderYawOffset) * 0.3F;
		GetLivingHeadLimitEvent ev = GetLivingHeadLimitEvent.create(living);
		MinecraftForge.EVENT_BUS.post(ev);
		float hLimit = ev.getHardLimit();
		float sLimit = ev.getSoftLimit();
		float theta = MathHelper.clamp(MathHelper.wrapDegrees(living.rotationYaw - living.renderYawOffset), -hLimit, hLimit);
		living.renderYawOffset = living.rotationYaw - theta;
		if (theta * theta > sLimit * sLimit) {
			living.renderYawOffset += theta * 0.2F;
		}
	}

	public static void onTravel(EntityLivingBase living, float strafe, float vertical, float forward) {
		if (living.isServerWorld() || living.canPassengerSteer()) {
			if (living.isInWater() && (!(living instanceof EntityPlayer) || !((EntityPlayer) living).capabilities.isFlying)) {
				water(living, strafe, vertical, forward);
			} else if (living.isInLava() && (!(living instanceof EntityPlayer) || !((EntityPlayer) living).capabilities.isFlying)) {
				lava(living, strafe, vertical, forward);
			} else if (living.isElytraFlying()) {
				elytra(living, strafe, vertical, forward);
			} else {
				regular(living, strafe, vertical, forward);
			}
		}
		living.prevLimbSwingAmount = living.limbSwingAmount;
		double dx = living.posX - living.prevPosX;
		double dy = living instanceof EntityFlying ? living.posY - living.prevPosY : 0.0D;
		double dz = living.posZ - living.prevPosZ;
		float distanceMoved = MathHelper.sqrt(dx * dx + dy * dy + dz * dz) * 4.0F;
		if (distanceMoved > 1.0F) {
			distanceMoved = 1.0F;
		}
		living.limbSwingAmount += (distanceMoved - living.limbSwingAmount) * 0.4F;
		living.limbSwing += living.limbSwingAmount;
	}

	private static final class GetWaterSlowDown {
		private GetWaterSlowDown() {}

		private static final MethodHandle MH = Access.builder(EntityLivingBase.class)
			.name("func_189749_co", "getWaterSlowDown")
			.rtype(float.class);

		private static float invoke(EntityLivingBase living) {
			try {
				return (float) MH.invokeExact(living);
			} catch (Throwable t) {
				throw Access.rethrow(t);
			}
		}
	}

	private static final class GetFallSound {
		private GetFallSound() {}

		private static final MethodHandle MH = Access.builder(EntityLivingBase.class)
			.name("func_184588_d", "getFallSound")
			.ptype(int.class)
			.rtype(SoundEvent.class);

		private static SoundEvent invoke(EntityLivingBase living, int distance) {
			try {
				return (SoundEvent) MH.invokeExact(living, distance);
			} catch (Throwable t) {
				throw Access.rethrow(t);
			}
		}
	}

	private static final class SetFlag {
		private SetFlag() {}

		private static final MethodHandle MH = Access.builder(Entity.class)
			.name("func_70052_a", "setFlag")
			.ptypes(int.class, boolean.class)
			.rtype(void.class);

		private static void invoke(Entity entity, int flag, boolean value) {
			try {
				MH.invokeExact(entity, flag, value);
			} catch (Throwable t) {
				throw Access.rethrow(t);
			}
		}
	}

	private static void water(EntityLivingBase living, float strafe, float vertical, float forward) {
		double originalY = living.posY;
		float invFriction = GetWaterSlowDown.invoke(living);
		float amount = 0.02F;
		int depthStriderLvl = Math.min(EnchantmentHelper.getDepthStriderModifier(living), 3);
		if (depthStriderLvl > 0) {
			float modifier = depthStriderLvl; 
			if (!living.onGround) {
				modifier *= 0.5F;
			}
			invFriction += (0.546F - invFriction) * modifier / 3.0F;
			amount += (living.getAIMoveSpeed() - amount) * modifier / 3.0F;
		}
		living.moveRelative(strafe, vertical, forward, amount);
		living.move(MoverType.SELF, living.motionX, living.motionY, living.motionZ);
		living.motionX *= invFriction;
		living.motionY *= 0.8D;
		living.motionZ *= invFriction;
		if (!living.hasNoGravity()) {
			living.motionY -= 0.02D;
		}
		if (living.collidedHorizontally && living.isOffsetPositionInLiquid(living.motionX, living.motionY + 0.6D + originalY - living.posY, living.motionZ)) {
			living.motionY = 0.3D;
		}
	}

	private static void elytra(EntityLivingBase living, float strafe, float vertical, float forward) {
		if (living.motionY > -0.5D) {
			living.fallDistance = 1.0F;
		}
		Vec3d vector = living.getLookVec();
		float inclination = Mth.toRadians(living.rotationPitch);
		double hVecMag = Math.sqrt(vector.x * vector.x + vector.z * vector.z);
		double hVelocity = Math.sqrt(living.motionX * living.motionX + living.motionZ * living.motionZ);
		float incY = MathHelper.cos(inclination);
		float incMag = (float) (incY * incY * Math.min(1.0D, vector.length() / 0.4D));
		living.motionY += -0.08D + incMag * 0.06D;
		if (living.motionY < 0.0D && hVecMag > 0.0D) {
			double magnitude = living.motionY * -0.1D * incMag;
			living.motionY += magnitude;
			living.motionX += vector.x * magnitude / hVecMag;
			living.motionZ += vector.z * magnitude / hVecMag;
		}
		if (inclination < 0.0F) {
			double magnitude = hVelocity * -MathHelper.sin(inclination) * 0.04D;
			living.motionY += magnitude * 3.2D;
			living.motionX -= vector.x * magnitude / hVecMag;
			living.motionZ -= vector.z * magnitude / hVecMag;
		}
		if (hVecMag > 0.0D) {
			living.motionX += (vector.x / hVecMag * hVelocity - living.motionX) * 0.1D;
			living.motionZ += (vector.z / hVecMag * hVelocity - living.motionZ) * 0.1D;
		}
		living.motionX *= 0.99D;
		living.motionY *= 0.98D;
		living.motionZ *= 0.99D;
		living.move(MoverType.SELF, living.motionX, living.motionY, living.motionZ);
		if (!living.world.isRemote) {
			if (living.collidedHorizontally) {
				double newHVelocity = Math.sqrt(living.motionX * living.motionX + living.motionZ * living.motionZ);
				float distance = (float) ((hVelocity - newHVelocity) * 10.0D - 3.0D);
				if (distance > 0.0F) {
					living.playSound(GetFallSound.invoke(living, (int) distance), 1.0F, 1.0F);
					living.attackEntityFrom(DamageSource.FLY_INTO_WALL, distance);
				}
			}
			if (living.onGround) {
				SetFlag.invoke(living, 7, false);
			}
		}
	}

	private static void regular(EntityLivingBase living, float strafe, float vertical, float forward) {
		float invFriction = 0.91F;
		BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain(living.posX, living.getEntityBoundingBox().minY - 1.0D, living.posZ);
		if (living.onGround) {
			IBlockState state = living.world.getBlockState(pos);
			invFriction = state.getBlock().getSlipperiness(state, living.world, pos, living) * 0.91F;
		}
		float amount;
		if (living.onGround) {
			amount = living.getAIMoveSpeed() * 0.16277136F / (invFriction * invFriction * invFriction);
		} else {
			amount = living.jumpMovementFactor;
		}
		living.moveRelative(strafe, vertical, forward, amount);
		invFriction = 0.91F;
		if (living.onGround) {
			IBlockState state = living.world.getBlockState(pos.setPos(living.posX, living.getEntityBoundingBox().minY - 1.0D, living.posZ));
			invFriction = state.getBlock().getSlipperiness(state, living.world, pos, living) * 0.91F;
		}
		if (living.isOnLadder()) {
			final float maxSpeed = 0.15F;
			living.motionX = MathHelper.clamp(living.motionX, -maxSpeed, maxSpeed);
			living.motionZ = MathHelper.clamp(living.motionZ, -maxSpeed, maxSpeed);
			living.fallDistance = 0.0F;
			if (living.motionY < -maxSpeed) {
				living.motionY = -maxSpeed;
			}
			if (living.isSneaking() && living instanceof EntityPlayer && living.motionY < 0.0D) {
				living.motionY = 0.0D;
			}
		}
		living.move(MoverType.SELF, living.motionX, living.motionY, living.motionZ);
		if (living.collidedHorizontally && living.isOnLadder()) {
			living.motionY = 0.2D;
		}
		if (living.isPotionActive(MobEffects.LEVITATION)) {
			living.motionY += (0.05D * (double) (living.getActivePotionEffect(MobEffects.LEVITATION).getAmplifier() + 1) - living.motionY) * 0.2D;
		} else {
			pos.setPos(living.posX, 0.0D, living.posZ);
			if (!living.world.isRemote || living.world.isBlockLoaded(pos) && living.world.getChunk(pos).isLoaded()) {
				if (!living.hasNoGravity()) {
					living.motionY -= 0.08D;
				}
			} else if (living.posY > 0.0D) {
				living.motionY = -0.1D;
			} else {
				living.motionY = 0.0D;
			}
		}
		living.motionY *= 0.98D;
		living.motionX *= invFriction;
		living.motionZ *= invFriction;
		pos.release();
	}

	private static void lava(EntityLivingBase living, float strafe, float vertical, float forward) {
		double originalY = living.posY;
		living.moveRelative(strafe, vertical, forward, 0.02F);
		living.move(MoverType.SELF, living.motionX, living.motionY, living.motionZ);
		living.motionX *= 0.5D;
		living.motionY *= 0.5D;
		living.motionZ *= 0.5D;
		if (!living.hasNoGravity()) {
			living.motionY -= 0.02D;
		}
		if (living.collidedHorizontally && living.isOffsetPositionInLiquid(living.motionX, living.motionY + 0.6000000238418579D - living.posY + originalY, living.motionZ)) {
			living.motionY = 0.3D;
		}
	}

	public static void onTurn(Entity entity, float deltaYaw) {
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) entity;
			float theta = MathHelper.wrapDegrees(living.rotationYaw - living.renderYawOffset);
			GetLivingHeadLimitEvent ev = GetLivingHeadLimitEvent.create(living);
			MinecraftForge.EVENT_BUS.post(ev);
			float limit = ev.getHardLimit();
			if (theta < -limit || theta > limit) {
				living.renderYawOffset += deltaYaw;
				living.prevRenderYawOffset += deltaYaw;
			}
		}
	}
}
