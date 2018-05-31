package com.pau101.wings.server.item.group;

import com.pau101.wings.WingsMod;
import com.pau101.wings.server.item.WingsItems;
import com.pau101.wings.server.item.StandardWing;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public final class ItemGroupWings extends CreativeTabs {
	public static final ItemGroupWings INSTANCE = new ItemGroupWings();

	private ItemGroupWings() {
		super(WingsMod.ID);
	}

	@Override
	public ItemStack getTabIconItem() {
		return WingsItems.WINGS.createStack(StandardWing.FAIRY);
	}
}
