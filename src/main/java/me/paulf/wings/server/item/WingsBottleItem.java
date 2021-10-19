package me.paulf.wings.server.item;

import me.paulf.wings.server.apparatus.FlightApparatus;
import me.paulf.wings.server.effect.WingsEffects;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.Flights;
import me.paulf.wings.server.sound.WingsSounds;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class WingsBottleItem extends Item {
    private final FlightApparatus wings;

    public WingsBottleItem(Properties properties, FlightApparatus wings) {
        super(properties);
        this.wings = wings;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity living) {
        if (living instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) living;
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            giveWing(player, this.wings);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), WingsSounds.ITEM_ARMOR_EQUIP_WINGS.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        if (living instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) living;
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.abilities.instabuild) {
                stack.shrink(1);
            }
        }
        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        if (living instanceof PlayerEntity && !((PlayerEntity) living).abilities.instabuild) {
            ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
            PlayerEntity player = (PlayerEntity) living;
            if (!player.inventory.add(emptyBottle)) {
                player.drop(emptyBottle, false);
            }
        }
        return stack;
    }

    public static boolean giveWing(ServerPlayerEntity player, FlightApparatus wings) {
        boolean changed = Flights.get(player).filter(flight -> {
            if (flight.getWing() != wings) {
                flight.setWing(wings, Flight.PlayerSet.ofAll());
                return true;
            }
            return false;
        }).isPresent();
        player.addEffect(new EffectInstance(WingsEffects.WINGS.get(), Integer.MAX_VALUE, 0, true, false));
        return changed;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        return DrinkHelper.useDrink(world, player, hand);
    }
}
