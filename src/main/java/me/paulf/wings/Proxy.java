package me.paulf.wings;

import me.paulf.wings.server.dreamcatcher.InSomniable;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.FlightDefault;
import me.paulf.wings.server.item.WingsItems;
import me.paulf.wings.server.net.Network;
import me.paulf.wings.server.net.clientbound.MessageSyncFlight;
import me.paulf.wings.server.potion.PotionMix;
import me.paulf.wings.server.potion.WingsPotion;
import me.paulf.wings.server.potion.WingsPotions;
import me.paulf.wings.util.SimpleStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potions;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.BiConsumer;

public abstract class Proxy {
    protected final Network network = new Network();

    public void init(IEventBus modBus) {
        modBus.addListener(this::setup);
    }

    protected void setup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(Flight.class, SimpleStorage.ofVoid(), FlightDefault::new);
        CapabilityManager.INSTANCE.register(InSomniable.class, SimpleStorage.ofVoid(), InSomniable::new);
        event.enqueueWork(() -> {
            BiConsumer<IItemProvider, RegistryObject<WingsPotion>> reg = (item, obj) -> BrewingRecipeRegistry.addRecipe(
                new PotionMix(Potions.SLOW_FALLING, Ingredient.of(item), obj.get().createStack())
            );
            reg.accept(Items.FEATHER, WingsPotions.ANGEL_WINGS);
            reg.accept(WingsItems.BAT_BLOOD.get(), WingsPotions.BAT_WINGS);
            reg.accept(Items.BLUE_DYE, WingsPotions.BLUE_BUTTERFLY_WINGS);
            reg.accept(Items.DRAGON_BREATH, WingsPotions.DRAGON_WINGS);
            reg.accept(Items.BONE, WingsPotions.EVIL_WINGS);
            reg.accept(Items.OXEYE_DAISY, WingsPotions.FAIRY_WINGS);
            reg.accept(Items.BLAZE_POWDER, WingsPotions.FIRE_WINGS);
            reg.accept(Items.ORANGE_DYE, WingsPotions.MONARCH_BUTTERFLY_WINGS);
            reg.accept(Items.SLIME_BALL, WingsPotions.SLIME_WINGS);
        });
    }

    public void addFlightListeners(PlayerEntity player, Flight instance) {
        if (player instanceof ServerPlayerEntity) {
            instance.registerFlyingListener(isFlying -> player.abilities.mayfly = isFlying);
            instance.registerFlyingListener(isFlying -> {
                if (isFlying) {
                    player.removeVehicle();
                }
            });
            Flight.Notifier notifier = Flight.Notifier.of(
                () -> this.network.sendToPlayer(new MessageSyncFlight(player, instance), (ServerPlayerEntity) player),
                p -> this.network.sendToPlayer(new MessageSyncFlight(player, instance), p),
                () -> this.network.sendToAllTracking(new MessageSyncFlight(player, instance), player)
            );
            instance.registerSyncListener(players -> players.notify(notifier));
        }
    }
}
