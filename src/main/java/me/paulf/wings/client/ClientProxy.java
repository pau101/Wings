package me.paulf.wings.client;

import me.paulf.wings.Proxy;
import me.paulf.wings.WingsMod;
import me.paulf.wings.client.flight.Animator;
import me.paulf.wings.client.flight.FlightView;
import me.paulf.wings.client.model.ModelWings;
import me.paulf.wings.client.model.ModelWingsAvian;
import me.paulf.wings.client.model.ModelWingsInsectoid;
import me.paulf.wings.client.renderer.LayerWings;
import me.paulf.wings.client.apparatus.WingForm;
import me.paulf.wings.client.apparatus.FlightApparatusView;
import me.paulf.wings.client.apparatus.FlightApparatusViews;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.animator.AnimatorAvian;
import me.paulf.wings.server.flight.animator.AnimatorInsectoid;
import me.paulf.wings.server.item.WingsItems;
import me.paulf.wings.server.net.serverbound.MessageControlFlying;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.SimpleStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ClientProxy extends Proxy {
	private final ModelWings<AnimatorAvian> avianWings = new ModelWingsAvian();

	private final ModelWings<AnimatorInsectoid> insectoidWings = new ModelWingsInsectoid();

	@Override
	public void preinit() {
		super.preinit();
		CapabilityManager.INSTANCE.register(FlightView.class, SimpleStorage.ofVoid(), () -> {
			throw new UnsupportedOperationException();
		});
		CapabilityManager.INSTANCE.register(FlightApparatusView.class, SimpleStorage.ofVoid(), () -> {
			throw new UnsupportedOperationException();
		});
	}

	@Override
	protected void init() {
		super.init();
		Minecraft mc = Minecraft.getMinecraft();
		ItemColors colors = mc.getItemColors();
		colors.registerItemColorHandler((stack, pass) -> pass == 0 ? 0x9B172D : 0xFFFFFF, WingsItems.BAT_BLOOD);
		for (RenderPlayer renderer : mc.getRenderManager().getSkinMap().values()) {
			renderer.addLayer(new LayerWings(renderer, (player, scale, bodyTransform) -> {
				if (player.isSneaking()) {
					GlStateManager.translate(0.0F, 0.2F, 0.0F);
				}
				bodyTransform.accept(scale);
			}));
		}
	}

	@Override
	public void addFlightListeners(EntityPlayer player, Flight flight) {
		super.addFlightListeners(player, flight);
		if (player.isUser()) {
			Flight.Notifier notifier = Flight.Notifier.of(
				() -> {},
				p -> {},
				() -> network.sendToServer(new MessageControlFlying(flight.isFlying()))
			);
			flight.registerSyncListener(players -> players.notify(notifier));
		}
	}

	@Override
	public Consumer<CapabilityProviders.CompositeBuilder> createAvianWings(String name) {
		return createWings(name, AnimatorAvian::new, avianWings);
	}

	@Override
	public Consumer<CapabilityProviders.CompositeBuilder> createInsectoidWings(String name) {
		return createWings(name, AnimatorInsectoid::new, insectoidWings);
	}

	private <A extends Animator> Consumer<CapabilityProviders.CompositeBuilder> createWings(String name, Supplier<A> animator, ModelWings<A> model) {
		WingForm<A> form = WingForm.of(
			animator,
			model,
			new ResourceLocation(WingsMod.ID, String.format("textures/entity/wings/%s.png", name))
		);
		return builder -> builder.add(FlightApparatusViews.providerBuilder(FlightApparatusViews.create(form)).build());
	}
}
