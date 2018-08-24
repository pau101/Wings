package me.paulf.wings.client.winged;

import me.paulf.wings.util.CapabilityHolder;
import me.paulf.wings.util.CapabilityProviders;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;

public final class FlightApparatusViews {
	private FlightApparatusViews() {}

	private static final CapabilityHolder<ItemStack, FlightApparatusView, WingedState> HOLDER = CapabilityHolder.create(WingedAbsentState::new, WingedPresentState::new);

	public static FlightApparatusView create(WingForm<?> form) {
		return () -> form;
	}

	public static boolean has(ItemStack stack) {
		return HOLDER.state().has(stack, null);
	}

	@Nullable
	public static FlightApparatusView get(ItemStack stack) {
		return HOLDER.state().get(stack, null);
	}

	public static <T extends FlightApparatusView> CapabilityProviders.NonSerializingSingleBuilder<T> providerBuilder(T instance) {
		return HOLDER.state().providerBuilder(instance);
	}

	@CapabilityInject(FlightApparatusView.class)
	static void inject(Capability<FlightApparatusView> capability) {
		HOLDER.inject(capability);
	}

	private interface WingedState extends CapabilityHolder.State<ItemStack, FlightApparatusView> {
		<T extends FlightApparatusView> CapabilityProviders.NonSerializingSingleBuilder<T> providerBuilder(T instance);
	}

	private static final class WingedAbsentState extends CapabilityHolder.AbsentState<ItemStack, FlightApparatusView> implements WingedState {
		@Override
		public <T extends FlightApparatusView> CapabilityProviders.NonSerializingSingleBuilder<T> providerBuilder(T instance) {
			return CapabilityProviders.emptyBuilder();
		}
	}

	private static final class WingedPresentState extends CapabilityHolder.PresentState<ItemStack, FlightApparatusView> implements WingedState {
		private WingedPresentState(Capability<FlightApparatusView> capability) {
			super(capability);
		}

		@Override
		public <T extends FlightApparatusView> CapabilityProviders.NonSerializingSingleBuilder<T> providerBuilder(T instance) {
			return CapabilityProviders.builder(capability, instance);
		}
	}
}
