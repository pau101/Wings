package me.paulf.wings.server.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.paulf.wings.WingsMod;
import me.paulf.wings.server.apparatus.FlightApparatus;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class WingsArgument implements ArgumentType<FlightApparatus> {
    private static final Collection<String> EXAMPLES = Arrays.asList("magical", "wings");
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_WING = new DynamicCommandExceptionType(e -> new TranslationTextComponent("wings.wingsNotFound", e));

    public WingsArgument() {
    }

    public static WingsArgument wings() {
        return new WingsArgument();
    }

    public static FlightApparatus getWings(CommandContext<CommandSource> ctx, String value) throws CommandSyntaxException {
        return ctx.getArgument(value, FlightApparatus.class);
    }

    @Override
    public FlightApparatus parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation key = ResourceLocation.read(reader);
        return WingsMod.WINGS.getOptional(key).orElseThrow(() -> ERROR_UNKNOWN_WING.create(key));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> ctx, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestResource(WingsMod.WINGS.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
