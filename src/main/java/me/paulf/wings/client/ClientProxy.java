package me.paulf.wings.client;

import me.paulf.wings.Proxy;
import me.paulf.wings.WingsMod;
import me.paulf.wings.client.apparatus.FlightApparatusView;
import me.paulf.wings.client.apparatus.FlightApparatusViews;
import me.paulf.wings.client.apparatus.WingForm;
import me.paulf.wings.client.flight.Animator;
import me.paulf.wings.client.flight.AnimatorAvian;
import me.paulf.wings.client.flight.AnimatorInsectoid;
import me.paulf.wings.client.flight.FlightView;
import me.paulf.wings.client.model.ModelWings;
import me.paulf.wings.client.model.ModelWingsAvian;
import me.paulf.wings.client.model.ModelWingsInsectoid;
import me.paulf.wings.client.renderer.LayerWings;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.Flights;
import me.paulf.wings.server.item.WingsItems;
import me.paulf.wings.server.net.serverbound.MessageControlFlying;
import me.paulf.wings.util.CapabilityProviders;
import me.paulf.wings.util.KeyInputListener;
import me.paulf.wings.util.SimpleStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

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
		MinecraftForge.EVENT_BUS.register(KeyInputListener.builder()
			.category("key.categories.wings")
				.key("key.wings.fly", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_R)
					.onPress(() -> {
						EntityPlayer player = Minecraft.getMinecraft().player;
						Flight flight = Flights.get(player);
						if (flight != null && flight.canFly(player)) {
							flight.toggleIsFlying(Flight.PlayerSet.ofOthers());
						}
					})
			.build()
		);
	}

	@Override
	protected void init() {
		super.init();
		final Minecraft mc = Minecraft.getMinecraft();
		final ItemColors colors = mc.getItemColors();
		colors.registerItemColorHandler((stack, pass) -> pass == 0 ? 0x9B172D : 0xFFFFFF, WingsItems.BAT_BLOOD);
		final RenderManager manager = mc.getRenderManager();
		Stream.concat(manager.getSkinMap().values().stream(), manager.entityRenderMap.values().stream())
			.filter(RenderLivingBase.class::isInstance)
			.map(RenderLivingBase.class::cast)
			.filter(render -> render.getMainModel() instanceof ModelBiped)
			.forEach(render -> {
				final ModelRenderer body = ((ModelBiped) render.getMainModel()).bipedBody;
				render.addLayer(new LayerWings(render, (player, scale) -> {
					if (player.isSneaking()) {
						GlStateManager.translate(0.0F, 0.2F, 0.0F);
					}
					body.postRender(scale);
				}));
			});
	}

	@Override
	public void addFlightListeners(final EntityPlayer player, final Flight flight) {
		super.addFlightListeners(player, flight);
		if (player.isUser()) {
			final Flight.Notifier notifier = Flight.Notifier.of(
				() -> {},
				p -> {},
				() -> this.network.sendToServer(new MessageControlFlying(flight.isFlying()))
			);
			flight.registerSyncListener(players -> players.notify(notifier));
		}
	}

	@Override
	public Consumer<CapabilityProviders.CompositeBuilder> createAvianWings(final String name) {
		return this.createWings(name, AnimatorAvian::new, this.avianWings);
	}

	@Override
	public Consumer<CapabilityProviders.CompositeBuilder> createInsectoidWings(final String name) {
		return this.createWings(name, AnimatorInsectoid::new, this.insectoidWings);
	}

	private <A extends Animator> Consumer<CapabilityProviders.CompositeBuilder> createWings(final String name, final Supplier<A> animator, final ModelWings<A> model) {
		final WingForm<A> form = WingForm.of(
			animator,
			model,
			new ResourceLocation(WingsMod.ID, String.format("textures/entity/wings/%s.png", name))
		);
		return builder -> builder.add(FlightApparatusViews.providerBuilder(FlightApparatusViews.create(form)).build());
	}
}
