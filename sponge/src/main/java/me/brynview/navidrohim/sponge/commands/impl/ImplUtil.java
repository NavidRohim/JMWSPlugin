package me.brynview.navidrohim.sponge.commands.impl;

import me.brynview.navidrohim.common.commands.SuggestionProvider;
import me.brynview.navidrohim.common.enums.ObjectType;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class ImplUtil {

    private static List<CommandCompletion> withCompletionListOf(CommandContext context, String currentInput, Function<UUID, List<String>> suggestFunc)
    {
        Optional<UUID> UUID = context.cause().audience().get(Identity.UUID);
        List<CommandCompletion> completions = new ArrayList<>();

        // UUID won't be present if the user is a console or anything without a UUID
        UUID.ifPresent(uuid -> suggestFunc.apply(uuid).forEach(wpString -> {
            if (wpString.contains(currentInput)) {
                completions.add(CommandCompletion.of(wpString));
            }
        }));
        return completions;

    }

    public static Parameter.Value.Builder<String> withCompletionListOfObjects(ObjectType completionType) {
        return Parameter.remainingJoinedStrings().completer(((context, currentInput) -> {
            Function<UUID, List<String>> suggestFunc = completionType == ObjectType.WAYPOINT ? SuggestionProvider::suggestWaypoints : SuggestionProvider::suggestGroups;
            return withCompletionListOf(context, currentInput, suggestFunc);
        }));
    }

    public static Parameter.Value.Builder<String> withCompletionListOfSharedObjects(ObjectType completionType)
    {
        return Parameter.remainingJoinedStrings().completer(((context, currentInput) -> {
            Function<UUID, List<String>> suggestFunc = completionType == ObjectType.WAYPOINT ? SuggestionProvider::suggestSharedWaypoints : SuggestionProvider::suggestSharedGroups;
            return withCompletionListOf(context, currentInput, suggestFunc);
        }));
    }

    public static Parameter.Value.Builder<String> withCompletionListOfGlobalObjects(ObjectType completionType)
    {
        return Parameter.remainingJoinedStrings().completer(((context, currentInput) -> {
            Function<UUID, List<String>> suggestFunc = completionType == ObjectType.WAYPOINT ? SuggestionProvider::suggestGlobalWaypoints : SuggestionProvider::suggestGlobalGroups;
            return withCompletionListOf(context, currentInput, suggestFunc);
        }));
    }
}
