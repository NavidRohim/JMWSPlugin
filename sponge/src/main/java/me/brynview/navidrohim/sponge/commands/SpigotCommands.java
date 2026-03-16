package me.brynview.navidrohim.sponge.commands;

import me.brynview.navidrohim.common.CommonClass;
import me.brynview.navidrohim.common.api.commands.Argument;
import me.brynview.navidrohim.common.api.commands.ArgumentTypes;
import me.brynview.navidrohim.common.commands.SuggestionProvider;
import me.brynview.navidrohim.common.enums.ObjectType;
import me.brynview.navidrohim.sponge.JMWSSponge;
import net.kyori.adventure.identity.Identity;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;

import java.util.*;
import java.util.function.Supplier;

public class SpigotCommands {

    @Listener
    public void onRegisterCommands(final RegisterCommandEvent<Command.Parameterized> event) {
        CommonClass.COMMANDS.forEach(command -> {this.registerCommand(command, event);});
    }

    // Don't ask me about the name.
    private static final Map<ArgumentTypes, Supplier<Parameter.Value.Builder<?>>> PRIMITIVE_TO_PRIVILEGED = Map.of(
            ArgumentTypes.STRING, Parameter::string,
            ArgumentTypes.WAYPOINT, SpigotCommands::waypoint,
            ArgumentTypes.GROUP, SpigotCommands::group
    );

    private static Parameter.Value.Builder<?> withCompletionListOf(ObjectType completionType) {
        return Parameter.string().completer((context, currentInput) -> {
            Optional<UUID> UUID = context.cause().audience().get(Identity.UUID);
            List<CommandCompletion> completions = new ArrayList<>();

            // UUID wont be present if the user is a console or anything without a UUID
            if (UUID.isPresent()) {
                List<String> waypoints = completionType == ObjectType.WAYPOINT ? SuggestionProvider.suggestWaypoints(UUID.get()) : SuggestionProvider.suggestGroups(UUID.get());

                waypoints.forEach(wpString -> {
                    if (wpString.contains(currentInput)) {
                        completions.add(CommandCompletion.of(wpString));
                    }
                });
            }
            return completions;
        });
    }

    private static Parameter.Value.Builder<?> waypoint() {
        return withCompletionListOf(ObjectType.WAYPOINT);
    }

    private static Parameter.Value.Builder<?> group() {
        return withCompletionListOf(ObjectType.GROUP);
    }

    private void registerCommand(me.brynview.navidrohim.common.api.commands.Command command, final RegisterCommandEvent<Command.Parameterized> event)
    {
        final List<Parameter> params = new ArrayList<>();

        for (Argument arg : command.commandArguments()) {
            params.add(PRIMITIVE_TO_PRIVILEGED.get(arg.type()).get().key(arg.name()).build());
        }

        Command.Parameterized nativeCommand = Command.builder().addParameters(params).build();
        event.register(JMWSSponge.getPlugin().container(), nativeCommand, command.commandName());
    }
}
