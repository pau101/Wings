package me.paulf.wings.client.apparatus;

import me.paulf.wings.util.CapabilityHolder;
import me.paulf.wings.util.CapabilityProviders;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;

public final class FlightApparatusViews {
	private FlightApparatusViews() {}

	private static final CapabilityHolder<ItemStack, FlightApparatusView, CapabilityHolder.State<ItemStack, FlightApparatusView>> HOLDER = CapabilityHolder.create();

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
}
