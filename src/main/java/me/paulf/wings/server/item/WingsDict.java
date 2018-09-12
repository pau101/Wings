package me.paulf.wings.server.item;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.block.WingsBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class WingsDict {
	private WingsDict() {}

	public static final String AMETHYST_ORE = "oreAmethyst";

	public static final String FAIRY_DUST = "dustFairy";

	public static final String AMETHYST_GEM = "gemAmethyst";

	@SubscribeEvent
	public static void onRegister(RegistryEvent.Register<IRecipe> event) {
		OreDictionary.registerOre(AMETHYST_ORE, WingsBlocks.AMETHYST_ORE);
		OreDictionary.registerOre(FAIRY_DUST, WingsItems.FAIRY_DUST);
		OreDictionary.registerOre(AMETHYST_GEM, WingsItems.AMETHYST);
	}

	public static boolean test(ItemStack stack, String name) {
		return ArrayUtils.contains(OreDictionary.getOreIDs(stack), OreDictionary.getOreID(name));
	}
}
