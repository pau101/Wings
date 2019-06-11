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

	public FeatureVein(final Supplier<IBlockState> block, final int size) {
		this(b -> block.get(), size);
	}

	public FeatureVein(final UnaryOperator<IBlockState> block, final int size) {
		this(block, size, StonePredicate.create());
	}

	public FeatureVein(final UnaryOperator<IBlockState> block, final int size, final Predicate<IBlockState> canReplace) {
		this.block = block;
		this.size = size;
		this.canReplace = canReplace;
	}

	@Override
	public boolean generate(final World world, final Random rng, final BlockPos pos) {
		final float yaw = rng.nextFloat() * (float) Math.PI;
		final double xExtent = MathHelper.sin(yaw) * this.size / 8.0F;
		final double zExtent = MathHelper.cos(yaw) * this.size / 8.0F;
		final double startX = pos.getX() + xExtent;
		final double endX = pos.getX() - xExtent;
		final double startZ = pos.getZ() + zExtent;
		final double endZ = pos.getZ() - zExtent;
		final double startY = pos.getY() + rng.nextInt(3) - 2;
		final double endY = pos.getY() + rng.nextInt(3) - 2;
		for (int n = 0; n < this.size; n++) {
			final float delta = (float) n / this.size;
			final double x = startX + (endX - startX) * delta;
			final double y = startY + (endY - startY) * delta;
			final double z = startZ + (endZ - startZ) * delta;
			final double girth = rng.nextDouble() * this.size / 16.0D;
			final double xzRadius = ((MathHelper.sin((float) Math.PI * delta) + 1.0F) * girth + 1.0D) / 2.0D;
			final double yRadius = ((MathHelper.sin((float) Math.PI * delta) + 1.0F) * girth + 1.0D) / 2.0D;
			final int minX = MathHelper.floor(x - xzRadius);
			final int minY = MathHelper.floor(y - yRadius);
			final int minZ = MathHelper.floor(z - xzRadius);
			final int maxX = MathHelper.floor(x + xzRadius);
			final int maxY = MathHelper.floor(y + yRadius);
			final int maxZ = MathHelper.floor(z + xzRadius);
			for (int dx = minX; dx <= maxX; dx++) {
				final double xDist = (dx + 0.5D - x) / xzRadius;
				if (xDist * xDist < 1.0D) {
					for (int dy = minY; dy <= maxY; dy++) {
						final double yDist = (dy + 0.5D - y) / yRadius;
						if (xDist * xDist + yDist * yDist < 1.0D) {
							for (int dz = minZ; dz <= maxZ; dz++) {
								final double zDist = (dz + 0.5D - z) / xzRadius;
								if (xDist * xDist + yDist * yDist + zDist * zDist < 1.0D) {
									final BlockPos setPos = new BlockPos(dx, dy, dz);
									final IBlockState state = world.getBlockState(setPos);
									if (state.getBlock().isReplaceableOreGen(state, world, setPos, this.canReplace::test)) {
										world.setBlockState(setPos, this.block.apply(state), 2);
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
		public boolean test(final IBlockState state) {
			return state != null && state.getBlock() == Blocks.STONE && state.getValue(BlockStone.VARIANT).isNatural();
		}

		public static StonePredicate create() {
			return new StonePredicate();
		}
	}
}
