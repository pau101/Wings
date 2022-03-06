package me.paulf.wings;

import com.mojang.serialization.Lifecycle;
import me.paulf.wings.client.ClientProxy;
import me.paulf.wings.server.ServerProxy;
import me.paulf.wings.server.apparatus.FlightApparatus;
import me.paulf.wings.server.apparatus.SimpleFlightApparatus;
import me.paulf.wings.server.config.WingsItemsConfig;
import me.paulf.wings.server.effect.WingsEffects;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.item.WingsItems;
import me.paulf.wings.server.sound.WingsSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(WingsMod.ID)
public final class WingsMod {
    public static final String ID = "wings";

    private static WingsMod INSTANCE;

    public static final Registry<FlightApparatus> WINGS = new DefaultedRegistry<>(Names.NONE.toString(), RegistryKey.createRegistryKey(new ResourceLocation(ID, "wings")), Lifecycle.experimental());

    public static final FlightApparatus NONE_WINGS = Registry.register(WINGS, Names.NONE, FlightApparatus.NONE);
    public static final FlightApparatus ANGEL_WINGS = Registry.register(WINGS, Names.ANGEL, new SimpleFlightApparatus(WingsItemsConfig.ANGEL));
	public static final FlightApparatus PARROT_WINGS = Registry.register(WINGS, Names.PARROT, new SimpleFlightApparatus(WingsItemsConfig.PARROT));
    public static final FlightApparatus BAT_WINGS = Registry.register(WINGS, Names.BAT, new SimpleFlightApparatus(WingsItemsConfig.BAT));
    public static final FlightApparatus BLUE_BUTTERFLY_WINGS = Registry.register(WINGS, Names.BLUE_BUTTERFLY, new SimpleFlightApparatus(WingsItemsConfig.BLUE_BUTTERFLY));
    public static final FlightApparatus DRAGON_WINGS = Registry.register(WINGS, Names.DRAGON, new SimpleFlightApparatus(WingsItemsConfig.DRAGON));
    public static final FlightApparatus EVIL_WINGS = Registry.register(WINGS, Names.EVIL, new SimpleFlightApparatus(WingsItemsConfig.EVIL));
    public static final FlightApparatus FAIRY_WINGS = Registry.register(WINGS, Names.FAIRY, new SimpleFlightApparatus(WingsItemsConfig.FAIRY));
    public static final FlightApparatus MONARCH_BUTTERFLY_WINGS = Registry.register(WINGS, Names.MONARCH_BUTTERFLY, new SimpleFlightApparatus(WingsItemsConfig.MONARCH_BUTTERFLY));
    public static final FlightApparatus SLIME_WINGS = Registry.register(WINGS, Names.SLIME, new SimpleFlightApparatus(WingsItemsConfig.SLIME));
    public static final FlightApparatus FIRE_WINGS = Registry.register(WINGS, Names.FIRE, new SimpleFlightApparatus(WingsItemsConfig.FIRE));

    private Proxy proxy;

    public WingsMod() {
        if (INSTANCE != null) throw new IllegalStateException("Already constructed!");
        INSTANCE = this;
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        WingsItems.REG.register(bus);
        WingsSounds.REG.register(bus);
        WingsEffects.REG.register(bus);
        this.proxy = DistExecutor.safeRunForDist(() -> ProxyInit::createClient, () -> ProxyInit::createServer);
        this.proxy.init(bus);
    }

    static class ProxyInit {
        static Proxy createClient() {
            return new ClientProxy();
        }

        static Proxy createServer() {
            return new ServerProxy();
        }
    }

    public void addFlightListeners(PlayerEntity player, Flight instance) {
        this.requireProxy().addFlightListeners(player, instance);
    }

    public static WingsMod instance() {
        return INSTANCE;
    }

    private Proxy requireProxy() {
        if (this.proxy == null) {
            throw new IllegalStateException("Proxy not initialized");
        }
        return this.proxy;
    }

    public static final class Names {
        private Names() {
        }

        public static final ResourceLocation
            NONE = create("none"),
            ANGEL = create("angel_wings"),
            PARROT = create("parrot_wings"),
            SLIME = create("slime_wings"),
            BLUE_BUTTERFLY = create("blue_butterfly_wings"),
            MONARCH_BUTTERFLY = create("monarch_butterfly_wings"),
            FIRE = create("fire_wings"),
            BAT = create("bat_wings"),
            FAIRY = create("fairy_wings"),
            EVIL = create("evil_wings"),
            DRAGON = create("dragon_wings");

        private static ResourceLocation create(String path) {
            return new ResourceLocation(ID, path);
        }
    }
}
