package me.paulf.wings.server.block;

import me.paulf.wings.util.HarvestLevel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;

public final class BlockWingsOre extends Block {
	private final int minExp;

	private final int maxExp;

	private BlockWingsOre(final Block.Properties properties, final int minExp, final int maxExp) {
		super(properties);
		this.minExp = minExp;
		this.maxExp = maxExp;
	}

	@Override
	public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch) {
		return MathHelper.nextInt(RANDOM, this.minExp, this.maxExp);
	}

	public static BlockWingsOre create(final int minExp, final int maxExp, final HarvestLevel harvestLevel) {
		return new BlockWingsOre(
			Properties.create(Material.ROCK)
				.hardnessAndResistance(3.0F, 5.0F)
				.harvestTool(ToolType.PICKAXE)
				.harvestLevel(harvestLevel.getValue()),
			minExp,
			maxExp
		);
	}
}
