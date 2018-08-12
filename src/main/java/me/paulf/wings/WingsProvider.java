package me.paulf.wings;

import me.paulf.wings.server.integration.baubles.WingsBaubleHandler;
import me.paulf.wings.util.ItemPlacing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.Loader;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public final class WingsProvider {
	private State state = State.of(
		s -> {
			if (Loader.isModLoaded("baubles")) {
				return State.of(UnaryOperator.identity(),
					equip -> {
						equip.accept(ItemPlacing.forArmor(EntityEquipmentSlot.CHEST));
						equip.accept(WingsBaubleHandler.createBodyPlacing());
					},
					event -> event.accept(WingsBaubleHandler.createEventListener())
				);
			}
			return State.of(UnaryOperator.identity(),
				equip -> equip.accept(ItemPlacing.forArmor(EntityEquipmentSlot.CHEST)),
				event -> {}
			);
		},
		equip -> {},
		event -> {}
	);

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

		static State of(
			UnaryOperator<State> next,
			Consumer<Consumer<ItemPlacing<EntityPlayer>>> equipment,
			Consumer<Consumer<Object>> events
		) {
			return new State() {
				@Override
				public State get() {
					return next.apply(this);
				}

				@Override
				public void addEquipmentPlacings(Consumer<ItemPlacing<EntityPlayer>> consumer) {
					equipment.accept(consumer);
				}

				@Override
				public void addEventListeners(Consumer<Object> consumer) {
					events.accept(consumer);
				}
			};
		}
	}
}
