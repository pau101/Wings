package me.paulf.wings.server.world.feature;

import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class FeatureVein extends WorldGenerator {
	private final UnaryOperator<IBlockState> block;

	private final int size;

	private final Predicate<IBlockState> canReplace;

	public FeatureVein(Supplier<IBlockState> block, int size) {
		this(b -> block.get(), size);
	}

	public FeatureVein(UnaryOperator<IBlockState> block, int size) {
		this(block, size, StonePredicate.create());
	}

	public FeatureVein(UnaryOperator<IBlockState> block, int size, Predicate<IBlockState> canReplace) {
		this.block = block;
		this.size = size;
		this.canReplace = canReplace;
	}

	@Override
	public boolean generate(World world, Random rng, BlockPos pos) {
		float yaw = rng.nextFloat() * (float) Math.PI;
		double xExtent = MathHelper.sin(yaw) * size / 8.0F;
		double zExtent = MathHelper.cos(yaw) * size / 8.0F;
		double startX = pos.getX() + xExtent;
		double endX = pos.getX() - xExtent;
		double startZ = pos.getZ() + zExtent;
		double endZ = pos.getZ() - zExtent;
		double startY = pos.getY() + rng.nextInt(3) - 2;
		double endY = pos.getY() + rng.nextInt(3) - 2;
		for (int n = 0; n < size; n++) {
			float delta = (float) n / size;
			double x = startX + (endX - startX) * delta;
			double y = startY + (endY - startY) * delta;
			double z = startZ + (endZ - startZ) * delta;
			double girth = rng.nextDouble() * size / 16.0D;
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
									if (state.getBlock().isReplaceableOreGen(state, world, setPos, canReplace::test)) {
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
		public boolean test(IBlockState state) {
			return state != null && state.getBlock() == Blocks.STONE && state.getValue(BlockStone.VARIANT).isNatural();
		}

		public static StonePredicate create() {
			return new StonePredicate();
		}
	}
}
