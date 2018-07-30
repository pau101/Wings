package com.pau101.wings.server.item.group;

import com.pau101.wings.WingsMod;
import com.pau101.wings.server.item.StandardWing;
import com.pau101.wings.server.item.WingsItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public final class ItemGroupWings extends CreativeTabs {
	private static final class Holder {
		private static final ItemGroupWings INSTANCE = new ItemGroupWings();	
	}

	private ItemGroupWings() {
		super(WingsMod.ID);
	}

	@Override
	public ItemStack createIcon() {
		return WingsItems.WINGS.createStack(StandardWing.FAIRY);
	}

	public static ItemGroupWings instance() {
		return Holder.INSTANCE;
	}
}
