package me.paulf.wings.client;

import me.paulf.wings.Proxy;
import me.paulf.wings.WingsMod;
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
import me.paulf.wings.util.KeyInputListener;
import me.paulf.wings.util.SimpleStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;
import java.util.stream.Stream;

public final class ClientProxy extends Proxy {
    private final ModelWings<AnimatorAvian> avianWings = new ModelWingsAvian();

    private final ModelWings<AnimatorInsectoid> insectoidWings = new ModelWingsInsectoid();

    @Override
    public void init(IEventBus modBus) {
        super.init(modBus);
        MinecraftForge.EVENT_BUS.register(KeyInputListener.builder()
            .category("key.categories.wings")
            .key("key.wings.fly", KeyConflictContext.IN_GAME, KeyModifier.NONE, GLFW.GLFW_KEY_R)
            .onPress(() -> {
                PlayerEntity player = Minecraft.getInstance().player;
                Flights.get(player).filter(flight -> flight.canFly(player)).ifPresent(flight ->
                    flight.toggleIsFlying(Flight.PlayerSet.ofOthers())
                );
            })
            .build()
        );
        modBus.<FMLClientSetupEvent>addListener(e -> {
            Minecraft mc = Minecraft.getInstance();
            EntityRendererManager manager = mc.getEntityRenderDispatcher();
            Stream.concat(manager.getSkinMap().values().stream(), manager.renderers.values().stream())
                .filter(LivingRenderer.class::isInstance)
                .map(r -> (LivingRenderer<?, ?>) r)
                .filter(render -> render.getModel() instanceof BipedModel<?>)
                .unordered()
                .distinct()
                .forEach(render -> {
                    ModelRenderer body = ((BipedModel<?>) render.getModel()).body;
                    @SuppressWarnings("unchecked") LivingRenderer<LivingEntity, BipedModel<LivingEntity>> livingRender = (LivingRenderer<LivingEntity, BipedModel<LivingEntity>>) render;
                    livingRender.addLayer(new LayerWings(livingRender, (player, stack) -> {
                        if (player.isCrouching()) {
                            stack.translate(0.0D, 0.2D, 0.0D);
                        }
                        body.translateAndRotate(stack);
                    }));
                });
            WingForm.register(WingsMod.ANGEL_WINGS, this.createAvianWings(WingsMod.WINGS.getKey(WingsMod.ANGEL_WINGS)));
            WingForm.register(WingsMod.BAT_WINGS, this.createAvianWings(WingsMod.WINGS.getKey(WingsMod.BAT_WINGS)));
            WingForm.register(WingsMod.BLUE_BUTTERFLY_WINGS, this.createInsectoidWings(WingsMod.WINGS.getKey(WingsMod.BLUE_BUTTERFLY_WINGS)));
            WingForm.register(WingsMod.DRAGON_WINGS, this.createAvianWings(WingsMod.WINGS.getKey(WingsMod.DRAGON_WINGS)));
            WingForm.register(WingsMod.EVIL_WINGS, this.createAvianWings(WingsMod.WINGS.getKey(WingsMod.EVIL_WINGS)));
            WingForm.register(WingsMod.FAIRY_WINGS, this.createInsectoidWings(WingsMod.WINGS.getKey(WingsMod.FAIRY_WINGS)));
            WingForm.register(WingsMod.MONARCH_BUTTERFLY_WINGS, this.createInsectoidWings(WingsMod.WINGS.getKey(WingsMod.MONARCH_BUTTERFLY_WINGS)));
            WingForm.register(WingsMod.SLIME_WINGS, this.createInsectoidWings(WingsMod.WINGS.getKey(WingsMod.SLIME_WINGS)));
        });
        modBus.<ColorHandlerEvent.Item>addListener(e -> {
            e.getItemColors().register((stack, pass) -> pass == 0 ? 0x9B172D : 0xFFFFFF, WingsItems.BAT_BLOOD.get());
        });
    }

    @Override
    protected void setup(FMLCommonSetupEvent event) {
        super.setup(event);
        CapabilityManager.INSTANCE.register(FlightView.class, SimpleStorage.ofVoid(), () -> {
            throw new UnsupportedOperationException();
        });
    }

    @Override
    public void addFlightListeners(PlayerEntity player, Flight flight) {
        super.addFlightListeners(player, flight);
        if (player.isLocalPlayer()) {
            Flight.Notifier notifier = Flight.Notifier.of(
                () -> {
                },
                p -> {
                },
                () -> this.network.sendToServer(new MessageControlFlying(flight.isFlying()))
            );
            flight.registerSyncListener(players -> players.notify(notifier));
        }
    }

    private WingForm<AnimatorAvian> createAvianWings(ResourceLocation name) {
        return this.createWings(name, AnimatorAvian::new, this.avianWings);
    }

    private WingForm<AnimatorInsectoid> createInsectoidWings(ResourceLocation name) {
        return this.createWings(name, AnimatorInsectoid::new, this.insectoidWings);
    }

    private <A extends Animator> WingForm<A> createWings(ResourceLocation name, Supplier<A> animator, ModelWings<A> model) {
        return WingForm.of(
            animator,
            model,
            new ResourceLocation(name.getNamespace(), String.format("textures/entity/%s.png", name.getPath()))
        );
    }
}
