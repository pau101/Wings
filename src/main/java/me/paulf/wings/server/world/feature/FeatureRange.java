package me.paulf.wings.server.world.feature;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public final class FeatureRange extends WorldGenerator {
	private final WorldGenerator feature;

	private final int count;

	private final int minHeight;

	private final int maxHeight;

	public FeatureRange(final WorldGenerator feature, final int count, final int minHeight, final int maxHeight) {
		this.feature = feature;
		this.count = count;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
	}

	@Override
	public boolean generate(final World world, final Random rng, final BlockPos pos) {
		boolean result = false;
		for (int n = this.count; n --> 0; ) {
			result |= this.feature.generate(world, rng, pos.add(
				rng.nextInt(16),
				rng.nextInt(this.maxHeight - this.minHeight) + this.minHeight,
				rng.nextInt(16)
			));
		}
		return result;
	}
}
