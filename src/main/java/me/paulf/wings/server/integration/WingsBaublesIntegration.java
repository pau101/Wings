package me.paulf.wings.server.integration;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.BaubleItem;
import baubles.api.cap.BaublesCapabilities;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import me.paulf.wings.WingsMod;
import me.paulf.wings.server.asm.plugin.Integration;
import me.paulf.wings.server.flight.ConstructWingsAccessorEvent;
import me.paulf.wings.server.item.ItemWings;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.ItemPlacing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;

@Integration(
	id = "bauble_wings",
	name = "Bauble Wings",
	condition = "required-after:wings;after:baubles@[1.5,1.6)"
)
public final class WingsBaublesIntegration {
	@Mod.EventHandler
	public void init(final FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new Object() {
			@SubscribeEvent
			public void onConstructWingsAccessor(final ConstructWingsAccessorEvent event) {
				event.addPlacing(new ItemPlacing<EntityPlayer>() {
					@Override
					public IItemHandler getStorage(final EntityPlayer player) {
						return BaublesApi.getBaublesHandler(player);
					}

					@Override
					public IntList getSlots() {
						return IntLists.unmodifiable(IntArrayList.wrap(BaubleType.BODY.getValidSlots()));
					}
				});
			}

			@SubscribeEvent
			public void onAttachCapabilities(final AttachCapabilitiesEvent<ItemStack> event) {
				if (event.getObject().getItem() instanceof ItemWings) {
					event.addCapability(
						new ResourceLocation(WingsMod.ID, TIFFANY),
						CapabilityProviders.builder(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, new BaubleItem(BaubleType.BODY)).build()
					);
				}
			}
		});
	}

	private static final String TIFFANY =
		"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" +
		"AAEAAAAATGF2ZjU4LjE3LjEwMQ==";
}
