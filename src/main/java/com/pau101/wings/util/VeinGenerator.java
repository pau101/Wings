package com.pau101.wings.util;

import java.util.Random;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.google.common.base.Predicate;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

@SuppressWarnings("Guava")
public final class VeinGenerator extends WorldGenerator {
	private final UnaryOperator<IBlockState> block;

	private final int blockCount;

	private final Predicate<IBlockState> canReplace;

	public VeinGenerator(Supplier<IBlockState> block, int count) {
		this(b -> block.get(), count);
	}

	public VeinGenerator(UnaryOperator<IBlockState> block, int count) {
		this(block, count, StonePredicate.create());
	}

	public VeinGenerator(UnaryOperator<IBlockState> block, int blockCount, Predicate<IBlockState> canReplace) {
		this.block = block;
		this.blockCount = blockCount;
		this.canReplace = canReplace;
	}

	@Override
	public boolean generate(World world, Random rng, BlockPos pos) {
		float yaw = rng.nextFloat() * (float) Math.PI;
		double xExtent = MathHelper.sin(yaw) * blockCount / 8.0F;
		double zExtent = MathHelper.cos(yaw) * blockCount / 8.0F;
		double startX = pos.getX() + xExtent;
		double endX = pos.getX() - xExtent;
		double startZ = pos.getZ() + zExtent;
		double endZ = pos.getZ() - zExtent;
		double startY = pos.getY() + rng.nextInt(3) - 2;
		double endY = pos.getY() + rng.nextInt(3) - 2;
		for (int n = 0; n < blockCount; n++) {
			float delta = (float) n / blockCount;
			double x = startX + (endX - startX) * delta;
			double y = startY + (endY - startY) * delta;
			double z = startZ + (endZ - startZ) * delta;
			double girth = rng.nextDouble() * blockCount / 16.0D;
			double xzRadius = ((MathHelper.sin((float) Math.PI * delta) + 1.0F) * girth + 1.0D) / 2.0D;
			double yRadius = ((MathHelper.sin((float) Math.PI * delta) + 1.0F) * girth + 1.0D) / 2.0D;
			int minX = MathHelper.floor(x - xzRadius);
			int minY = MathHelper.floor(y - yRadius);
			int minZ = MathHelper.floor(z - xzRadius);
			int maxX = MathHelper.floor(x + xzRadius);
			int maxY = MathHelper.floor(y + yRadius);
			int maxZ = MathHelper.floor(z + xzRadius);
			for (int dx = minX; dx <= maxX; dx++) {
				double xDist = (dx + 0.5D - x) / xzRadius;
				if (xDist * xDist < 1.0D) {
					for (int dy = minY; dy <= maxY; dy++) {
						double yDist = (dy + 0.5D - y) / yRadius;
						if (xDist * xDist + yDist * yDist < 1.0D) {
							for (int dz = minZ; dz <= maxZ; dz++) {
								double zDist = (dz + 0.5D - z) / xzRadius;
								if (xDist * xDist + yDist * yDist + zDist * zDist < 1.0D) {
									BlockPos setPos = new BlockPos(dx, dy, dz);
									IBlockState state = world.getBlockState(setPos);
									if (state.getBlock().isReplaceableOreGen(state, world, setPos, canReplace)) {
										world.setBlockState(setPos, block.apply(state), 2);
									}
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	public static final class StonePredicate implements Predicate<IBlockState> {
		private StonePredicate() {}

		@Override
		public boolean apply(IBlockState state) {
			return state != null && state.getBlock() == Blocks.STONE && state.getValue(BlockStone.VARIANT).isNatural();
		}

		public static StonePredicate create() {
			return new StonePredicate();
		}
	}
}
