package me.paulf.wings.server.item.group;

import me.paulf.wings.WingsMod;
import me.paulf.wings.server.item.WingsItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public final class ItemGroupWings extends ItemGroup {
	private static final class Holder {
		private static final ItemGroupWings INSTANCE = new ItemGroupWings();	
	}

	private ItemGroupWings() {
		super(WingsMod.ID);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(WingsItems.ANGEL_WINGS.get());
	}

	public static ItemGroupWings instance() {
		return Holder.INSTANCE;
	}
}
