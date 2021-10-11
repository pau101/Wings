package me.paulf.wings.server.item;

import me.paulf.wings.server.apparatus.FlightApparatus;
import me.paulf.wings.server.flight.Flights;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class WingsBottleItem extends Item {
    private final FlightApparatus wings;

    public WingsBottleItem(Properties properties, FlightApparatus wings) {
        super(properties);
        this.wings = wings;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity living) {
        super.finishUsingItem(stack, world, living);
        if (living instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) living;
            Flights.get(player).ifPresent(flight -> {
                flight.setWing(this.wings);
            });
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        if (living instanceof PlayerEntity && !((PlayerEntity)living).abilities.instabuild) {
            ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
            PlayerEntity player = (PlayerEntity) living;
            if (!player.inventory.add(emptyBottle)) {
                player.drop(emptyBottle, false);
            }
        }
        return stack;
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
