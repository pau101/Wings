package me.paulf.wings.server.block;

import me.paulf.wings.WingsMod;
import me.paulf.wings.util.HarvestLevel;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class WingsBlocks {
	private WingsBlocks() {}

	public static final DeferredRegister<Block> REG = DeferredRegister.create(ForgeRegistries.BLOCKS, WingsMod.ID);

//	public static final RegistryObject<Block> FAIRY_DUST_ORE = REG.register("fairy_dust_ore", () -> BlockWingsOre.create(0, 2, HarvestLevel.STONE));

//	public static final RegistryObject<Block> AMETHYST_ORE = REG.register("amethyst_ore", () -> BlockWingsOre.create(3, 7, HarvestLevel.IRON));
}
