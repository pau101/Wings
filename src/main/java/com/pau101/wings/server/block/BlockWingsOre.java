package com.pau101.wings.server.block;

import java.util.Random;
import java.util.function.Supplier;

import com.pau101.wings.server.item.group.ItemGroupWings;
import com.pau101.wings.util.HarvestClass;
import com.pau101.wings.util.HarvestLevel;
import com.pau101.wings.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockWingsOre extends Block {
	private final Supplier<Item> drop;

	private final int minExp;

	private final int maxExp;

	public BlockWingsOre(Supplier<Item> drop, int minExp, int maxExp, HarvestLevel harvestLevel) {
		super(Material.ROCK);
		this.drop = drop;
		this.minExp = minExp;
		this.maxExp = maxExp;
		setHardness(3.0F);
		setResistance(5.0F);
		Util.setHarvestLevel(this, HarvestClass.PICKAXE, harvestLevel);
		setCreativeTab(ItemGroupWings.INSTANCE);
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random rng) {
		if (fortune > 0) {
			return quantityDropped(rng) * Math.max(rng.nextInt(fortune + 2), 1);
		}
		return quantityDropped(rng);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rng, int fortune) {
		return drop.get();
	}

	@Override
	public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
		return MathHelper.getInt(world instanceof World ? ((World) world).rand : new Random(), minExp, maxExp);
	}
}
