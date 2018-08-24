package me.paulf.wings.server;

import me.paulf.wings.Proxy;
import me.paulf.wings.util.CapabilityProviders;

import java.util.function.Consumer;

public final class ServerProxy extends Proxy {
	@Override
	public Consumer<CapabilityProviders.CompositeBuilder> createAvianWings(String name) {
		return builder -> {};
	}

	@Override
	public Consumer<CapabilityProviders.CompositeBuilder> createInsectoidWings(String name) {
		return builder -> {};
	}
}
