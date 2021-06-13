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

    public static final Registry<FlightApparatus> WINGS = new DefaultedRegistry<>(ID + ":angel_wings", RegistryKey.createRegistryKey(new ResourceLocation(ID, "wings")), Lifecycle.experimental());

    public static final FlightApparatus ANGEL_WINGS = Registry.register(WINGS, ID + ":angel_wings", new SimpleFlightApparatus(WingsItemsConfig.ANGEL));
    public static final FlightApparatus BAT_WINGS = Registry.register(WINGS, ID + ":bat_wings", new SimpleFlightApparatus(WingsItemsConfig.BAT));
    public static final FlightApparatus BLUE_BUTTERFLY_WINGS = Registry.register(WINGS, ID + ":blue_butterfly_wings", new SimpleFlightApparatus(WingsItemsConfig.BLUE_BUTTERFLY));
    public static final FlightApparatus DRAGON_WINGS = Registry.register(WINGS, ID + ":dragon_wings", new SimpleFlightApparatus(WingsItemsConfig.DRAGON));
    public static final FlightApparatus EVIL_WINGS = Registry.register(WINGS, ID + ":evil_wings", new SimpleFlightApparatus(WingsItemsConfig.EVIL));
    public static final FlightApparatus FAIRY_WINGS = Registry.register(WINGS, ID + ":fairy_wings", new SimpleFlightApparatus(WingsItemsConfig.FAIRY));
    public static final FlightApparatus MONARCH_BUTTERFLY_WINGS = Registry.register(WINGS, ID + ":monarch_butterfly_wings", new SimpleFlightApparatus(WingsItemsConfig.MONARCH_BUTTERFLY));
    public static final FlightApparatus SLIME_WINGS = Registry.register(WINGS, ID + ":slime_wings", new SimpleFlightApparatus(WingsItemsConfig.SLIME));

    private Proxy proxy;

    public WingsMod() {
        if (INSTANCE != null) throw new IllegalStateException("Already constructed!");
        INSTANCE = this;
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        WingsItems.REG.register(bus);
        WingsSounds.REG.register(bus);
        WingsEffects.REG.register(bus);
        this.proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
        this.proxy.init(bus);
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
}
