package com.pau101.wings.server.world;

import java.util.Random;

import com.pau101.wings.WingsMod;
import com.pau101.wings.server.block.WingsBlocks;
import com.pau101.wings.util.VeinGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class GenerationHandler {
	private GenerationHandler() {}

	private static final VeinGenerator FAIRY_DUST_ORE_GENERATOR = new VeinGenerator(WingsBlocks.FAIRY_DUST_ORE::getDefaultState, 9);

	private static final VeinGenerator AMETHYST_ORE_GENERATOR = new VeinGenerator(WingsBlocks.AMETHYST_ORE::getDefaultState, 8);

	@SubscribeEvent
	public static void onDecorateBiome(DecorateBiomeEvent.Pre event) {
		World world = event.getWorld();
		Random rng = event.getRand();
		BlockPos chunkPos = event.getPos();
		generate(world, rng, chunkPos, 10, FAIRY_DUST_ORE_GENERATOR, 0, 64);
		generate(world, rng, chunkPos, 1, AMETHYST_ORE_GENERATOR, 0, 16);
	}

	private static void generate(World world, Random rng, BlockPos chunkPos, int blockCount, WorldGenerator generator, int minHeight, int maxHeight) {
		for (int n = blockCount; n --> 0; ) {
			BlockPos pos = chunkPos.add(
				rng.nextInt(16) + 8,
				rng.nextInt(maxHeight - minHeight) + minHeight,
				rng.nextInt(16) + 8
			);
			generator.generate(world, rng, pos);
		}
	}
}
