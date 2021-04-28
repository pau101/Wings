package me.paulf.wings.server.item;

import me.paulf.wings.WingsMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class WingsItems {
	private WingsItems() {}

	public static final DeferredRegister<Item> REG = DeferredRegister.create(ForgeRegistries.ITEMS, WingsMod.ID);

	public static final RegistryObject<Item> BAT_BLOOD = REG.register("bat_blood", () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).tab(ItemGroup.TAB_FOOD)));

	public static final class Names {
		private Names() {}

		public static final ResourceLocation
			ANGEL = create("angel_wings"),
			SLIME = create("slime_wings"),
			BLUE_BUTTERFLY = create("blue_butterfly_wings"),
			MONARCH_BUTTERFLY = create("monarch_butterfly_wings"),
			FIRE = create("fire_wings"),
			BAT = create("bat_wings"),
			FAIRY = create("fairy_wings"),
			EVIL = create("evil_wings"),
			DRAGON = create("dragon_wings");

		private static ResourceLocation create(final String path) {
			return new ResourceLocation(WingsMod.ID, path);
		}
	}
}
