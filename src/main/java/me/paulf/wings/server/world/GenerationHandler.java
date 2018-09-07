package me.paulf.wings.server.world;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.block.WingsBlocks;
import me.paulf.wings.server.config.VeinSettings;
import me.paulf.wings.server.config.WingsOreConfig;
import me.paulf.wings.server.world.feature.FeatureRange;
import me.paulf.wings.server.world.feature.FeatureVein;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class GenerationHandler {
	private GenerationHandler() {}

	private static final WorldGenerator FAIRY_DUST_ORE_GENERATOR = newVeinFeature(
		WingsOreConfig.FAIRY_DUST,
		WingsBlocks.FAIRY_DUST_ORE::getDefaultState
	);

	private static final WorldGenerator AMETHYST_ORE_GENERATOR = newVeinFeature(
		WingsOreConfig.AMETHYST,
		WingsBlocks.AMETHYST_ORE::getDefaultState
	);

	@SubscribeEvent
	public static void onDecorateBiome(DecorateBiomeEvent.Pre event) {
		World world = event.getWorld();
		Random rng = event.getRand();
		BlockPos pos = event.getChunkPos().getBlock(8, 0, 8);
		FAIRY_DUST_ORE_GENERATOR.generate(world, rng, pos);
		AMETHYST_ORE_GENERATOR.generate(world, rng, pos);
	}

	private static WorldGenerator newVeinFeature(VeinSettings settings, Supplier<IBlockState> block) {
		return new FeatureRange(
			new FeatureVein(block, settings.getSize()),
			settings.getCount(),
			settings.getMinHeight(),
			settings.getMaxHeight()
		);
	}
}
