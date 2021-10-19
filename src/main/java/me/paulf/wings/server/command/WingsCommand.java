package me.paulf.wings.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.paulf.wings.server.apparatus.FlightApparatus;
import me.paulf.wings.server.item.BatBloodBottleItem;
import me.paulf.wings.server.item.WingsBottleItem;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class WingsCommand {
    private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.wings.give.failed"));

    private static final SimpleCommandExceptionType ERROR_TAKE_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.wings.take.failed"));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("wings")
            .then(literal("give")
                .then(argument("targets", EntityArgument.players())
                    .then(argument("wings", WingsArgument.wings())
                        .executes(WingsCommand::giveWing))))
            .then(literal("take")
                .then(argument("targets", EntityArgument.players())
                    .then(argument("wings", WingsArgument.wings()).executes(WingsCommand::takeSpecificWings))
                    .executes(WingsCommand::takeWings))));
    }

    private static int giveWing(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgument.getPlayers(ctx, "targets");
        FlightApparatus wings = WingsArgument.getWings(ctx, "wings");
        int count = 0;
        for (ServerPlayerEntity player : targets) {
            if (WingsBottleItem.giveWing(player, wings)) {
                count++;
            }
        }
        if (count == 0) {
            throw ERROR_GIVE_FAILED.create();
        }
        if (targets.size() == 1) {
            ctx.getSource().sendSuccess(new TranslationTextComponent("commands.wings.give.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            ctx.getSource().sendSuccess(new TranslationTextComponent("commands.wings.give.success.multiple", targets.size()), true);
        }
        return count;
    }

    private static int takeWings(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgument.getPlayers(ctx, "targets");
        int count = 0;
        for (ServerPlayerEntity player : targets) {
            if (BatBloodBottleItem.removeWings(player)) {
                count++;
            }
        }
        if (count == 0) {
            throw ERROR_TAKE_FAILED.create();
        }
        if (targets.size() == 1) {
            ctx.getSource().sendSuccess(new TranslationTextComponent("commands.wings.take.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            ctx.getSource().sendSuccess(new TranslationTextComponent("commands.wings.take.success.multiple", targets.size()), true);
        }
        return count;
    }

    private static int takeSpecificWings(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgument.getPlayers(ctx, "targets");
        FlightApparatus wings = WingsArgument.getWings(ctx, "wings");
        int count = 0;
        for (ServerPlayerEntity player : targets) {
            if (BatBloodBottleItem.removeWings(player, wings)) {
                count++;
            }
        }
        if (count == 0) {
            throw ERROR_TAKE_FAILED.create();
        }
        if (targets.size() == 1) {
            ctx.getSource().sendSuccess(new TranslationTextComponent("commands.wings.take.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            ctx.getSource().sendSuccess(new TranslationTextComponent("commands.wings.take.success.multiple", targets.size()), true);
        }
        return count;
    }
}
