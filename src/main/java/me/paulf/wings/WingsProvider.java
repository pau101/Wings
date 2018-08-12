package me.paulf.wings;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import me.paulf.wings.server.integration.baubles.WingsBaubleHandler;
import me.paulf.wings.util.ItemPlacing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Consumer;

public final class WingsProvider {
	private State state = new UninitializedState();

	public void addEventListeners(Consumer<Object> consumer) {
		getState().addEventListeners(consumer);
	}

	public void addEquipmentPlacings(Consumer<ItemPlacing<EntityPlayer>> consumer) {
		getState().addEquipmentPlacings(consumer);
	}

	private State getState() {
		return state = state.get();
	}

	private interface State {
		State get();

		void addEquipmentPlacings(Consumer<ItemPlacing<EntityPlayer>> consumer);

		void addEventListeners(Consumer<Object> consumer);
	}

	private static final class UninitializedState implements State {
		private static final String BAUBLES_ID = "baubles";

		@Override
		public State get() {
			final State basic = new BasicState();
			return Loader.isModLoaded(BAUBLES_ID) ? new BaublesState(basic) : basic;
		}

		@Override
		public void addEquipmentPlacings(Consumer<ItemPlacing<EntityPlayer>> consumer) {}

		@Override
		public void addEventListeners(Consumer<Object> consumer) {}
	}

	private static final class BasicState implements State {
		@Override
		public State get() {
			return this;
		}

		@Override
		public final void addEquipmentPlacings(Consumer<ItemPlacing<EntityPlayer>> consumer) {
			consumer.accept(new ItemPlacing<EntityPlayer>() {
				@Override
				public IItemHandler getStorage(EntityPlayer player) {
					return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
				}

				@Override
				public IntList getSlots() {
					return IntLists.singleton(EntityEquipmentSlot.CHEST.getIndex());
				}
			});
		}

		@Override
		public void addEventListeners(Consumer<Object> consumer) {}
	}

	private static final class BaublesState implements State {
		private final State parent;

		private BaublesState(State parent) {
			this.parent = parent;
		}

		@Override
		public State get() {
			return this;
		}

		@Override
		public void addEquipmentPlacings(Consumer<ItemPlacing<EntityPlayer>> consumer) {
			this.parent.addEquipmentPlacings(consumer);
			consumer.accept(WingsBaubleHandler.createBodyPlacing());
		}

		@Override
		public void addEventListeners(Consumer<Object> consumer) {
			consumer.accept(WingsBaubleHandler.createEventListener());
			this.parent.addEventListeners(consumer);
		}
	}
}
