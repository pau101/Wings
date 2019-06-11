package me.paulf.wings.server.block;

import me.paulf.wings.util.HarvestClass;
import me.paulf.wings.util.HarvestLevel;
import me.paulf.wings.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;
import java.util.function.Supplier;

public final class BlockWingsOre extends Block {
	private final Supplier<Item> drop;

	private final int minExp;

	private final int maxExp;

	private BlockWingsOre(final Supplier<Item> drop, final int minExp, final int maxExp) {
		super(Material.ROCK);
		this.drop = drop;
		this.minExp = minExp;
		this.maxExp = maxExp;
	}

	@Override
	public int quantityDroppedWithBonus(final int fortune, final Random rng) {
		return fortune > 0 ? this.quantityDropped(rng) * Math.max(rng.nextInt(fortune + 2), 1) : this.quantityDropped(rng);
	}

	@Override
	public Item getItemDropped(final IBlockState state, final Random rng, final int fortune) {
		return this.drop.get();
	}

	@Override
	public int getExpDrop(final IBlockState state, final IBlockAccess world, final BlockPos pos, final int fortune) {
		return MathHelper.getInt(world instanceof World ? ((World) world).rand : new Random(), this.minExp, this.maxExp);
	}

	public static BlockWingsOre create(final Supplier<Item> drop, final int minExp, final int maxExp, final HarvestLevel harvestLevel) {
		final BlockWingsOre block = new BlockWingsOre(drop, minExp, maxExp);
		block.setHardness(3.0F);
		block.setResistance(5.0F);
		Util.setHarvestLevel(block, HarvestClass.PICKAXE, harvestLevel);
		return block;
	}
}
