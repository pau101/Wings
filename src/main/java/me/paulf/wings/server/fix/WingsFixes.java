package me.paulf.wings.server.fix;

import me.paulf.wings.WingsMod;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class WingsFixes {
	private WingsFixes() {}

	private static final int DATA_VERSION = 1;

	private static final String WINGS = "wings:wings";

	private static final ResourceLocation WINGS_KEY = new ResourceLocation(WINGS);

	@SubscribeEvent
	public static void onMissingMappings(final RegistryEvent.MissingMappings<Item> event) {
		event.getMappings().stream()
			.filter(mapping -> WINGS_KEY.equals(mapping.key))
			.forEach(RegistryEvent.MissingMappings.Mapping::ignore);
	}

	public static void register() {
		FMLCommonHandler.instance().getDataFixer().registerWalker(FixTypes.PLAYER, (fixer, compound, version) -> {
			if (compound.hasKey("ForgeCaps", Constants.NBT.TAG_COMPOUND)) {
				final NBTTagCompound caps = compound.getCompoundTag("ForgeCaps");
				if (caps.hasKey("baubles:container", Constants.NBT.TAG_COMPOUND)) {
					DataFixesManager.processInventory(fixer, caps.getCompoundTag("baubles:container"), version, "Items");
				}
			}
			return compound;
		});
		final ModFixs fixer = FMLCommonHandler.instance().getDataFixer().init(WingsMod.ID, DATA_VERSION);
		fixer.registerFix(FixTypes.ITEM_INSTANCE, new IFixableData() {
			private final String[] lookup = {
				"wings:angel_wings",
				"wings:slime_wings",
				"wings:blue_butterfly_wings",
				"wings:monarch_butterfly_wings",
				"wings:fire_wings",
				"wings:bat_wings",
				"wings:fairy_wings",
				"wings:evil_wings",
				"wings:dragon_wings"
			};

			@Override
			public int getFixVersion() {
				return 1;
			}

			@Override
			public NBTTagCompound fixTagCompound(final NBTTagCompound compound) {
				if (compound.hasKey("id", Constants.NBT.TAG_STRING) && WINGS.equals(compound.getString("id"))) {
					final int damage = compound.getShort("Damage");
					compound.setString("id", this.lookup[damage >= 0 && damage < this.lookup.length ? damage : 0]);
					compound.setShort("Damage", (short) 0);
				}
				return compound;
			}
		});
	}
}
