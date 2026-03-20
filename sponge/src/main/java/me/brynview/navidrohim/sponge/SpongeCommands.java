package me.brynview.navidrohim.sponge;

import me.brynview.navidrohim.common.CommonClass;
import me.brynview.navidrohim.common.api.WSPlayer;
import me.brynview.navidrohim.common.commands.ServerCommands;
import me.brynview.navidrohim.common.commands.SuggestionProvider;
import me.brynview.navidrohim.common.enums.ObjectType;
import me.brynview.navidrohim.sponge.impl.SpongePlayer;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueCompleter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class SpongeCommands {

    // Argument completers

    private static List<CommandCompletion> objectCompleter(CommandContext commandContext, String currentString, Function<UUID, List<String>> chooseFrom) {
        Optional<UUID> senderUUID = commandContext.cause().audience().get(Identity.UUID);
        List<CommandCompletion> completions = new ArrayList<>();

        senderUUID.ifPresent(uuid -> chooseFrom.apply(uuid).forEach(identifier -> {
            if (identifier.contains(currentString)) {
                completions.add(CommandCompletion.of(identifier));
            }
        }));

        return completions;
    }

    private static List<CommandCompletion> waypointCompleter(CommandContext commandContext, String currentString) {
        return objectCompleter(commandContext, currentString, SuggestionProvider::suggestWaypoints);
    }

    private static List<CommandCompletion> groupCompleter(CommandContext commandContext, String currentString) {
        return objectCompleter(commandContext, currentString, SuggestionProvider::suggestGroups);
    }

    private static List<CommandCompletion> sharedGroupCompleter(CommandContext commandContext, String currentString) {
        return objectCompleter(commandContext, currentString, SuggestionProvider::suggestSharedGroups);
    }

    private static List<CommandCompletion> sharedWaypointCompleter(CommandContext commandContext, String currentString) {
        return objectCompleter(commandContext, currentString, SuggestionProvider::suggestSharedWaypoints);
    }

    private static List<CommandCompletion> globalGroupCompleter(CommandContext commandContext, String currentString) {
        return objectCompleter(commandContext, currentString, SuggestionProvider::suggestGlobalGroups);
    }

    private static List<CommandCompletion> globalWaypointCompleter(CommandContext commandContext, String currentString) {
        return objectCompleter(commandContext, currentString, SuggestionProvider::suggestGlobalWaypoints);
    }

    // Parameters

    private static final Parameter.Value<String> waypointParameter = Parameter.remainingJoinedStrings().completer(SpongeCommands::waypointCompleter).key("waypoint").build();
    private static final Parameter.Value<String> groupParameter = Parameter.remainingJoinedStrings().completer(SpongeCommands::groupCompleter).key("group").build();

    private static final Parameter.Value<String> sharedWaypointParameter = Parameter.remainingJoinedStrings().completer(SpongeCommands::sharedWaypointCompleter).key("shared_waypoint").build();
    private static final Parameter.Value<String> sharedGroupParameter = Parameter.remainingJoinedStrings().completer(SpongeCommands::sharedGroupCompleter).key("shared_group").build();

    private static final Parameter.Value<String> globalWaypointParameter = Parameter.remainingJoinedStrings().completer(SpongeCommands::globalWaypointCompleter).key("global_waypoint").build();
    private static final Parameter.Value<String> globalGroupParameter = Parameter.remainingJoinedStrings().completer(SpongeCommands::globalGroupCompleter).key("global_group").build();

    private static final Parameter.Value<ServerPlayer> playerParam = Parameter.player().key("player").build();

    private static CommandResult notValidUser()
    {
        return CommandResult.error(Component.text("You must be a player to use this command."));
    }

    private record CommandNameWrapper(Command.Parameterized command, String name) {}

    public static void register(final RegisterCommandEvent<Command.Parameterized> event, PluginContainer container) {
        // TODO Add autocompletion results to object parameters

        List<CommandNameWrapper> commands = List.of(
                new CommandNameWrapper(
                        Command.builder()
                                .addParameter(playerParam)
                                .addParameter(waypointParameter)
                                .executor(SpongeCommands::shareWaypoint)
                                .build(),
                        "share_waypoint"
                ),
                new CommandNameWrapper(
                        Command.builder()
                                .addParameter(playerParam)
                                .addParameter(groupParameter)
                                .executor(SpongeCommands::shareGroup)
                                .build(),
                        "share_group"
                ),
                new CommandNameWrapper(
                        Command.builder()
                                .addParameter(sharedWaypointParameter)
                                .executor(SpongeCommands::stopSharingWaypoint)
                                .build(),
                        "stop_sharing_waypoint"
                ),
                new CommandNameWrapper(
                        Command.builder()
                                .addParameter(sharedGroupParameter)
                                .executor(SpongeCommands::stopSharingGroup)
                                .build(),
                        "stop_sharing_group"
                )
        );

        List<CommandNameWrapper> adminCommands = List.of(

        );

        for (CommandNameWrapper command : commands) {
            event.register(container, command.command(), command.name());
        }

        Command.Builder adminCommandParent = Command.builder();
        Command.Parameterized adminCommandBuilt;
        for (CommandNameWrapper command : adminCommands) {
            adminCommandParent.addChild(command.command(),  command.name());
        }
        adminCommandBuilt = adminCommandParent.permission("minecraft.command.op").executor(ctx -> CommandResult.error(Component.text("Cannot execute directly. Use sub-command."))).build();

        event.register(container, adminCommandBuilt, "jmws_admin");
    }

    // Boilerplate command argument prep, actual logic happens in ServerCommands class

    private static CommandResult shareWaypoint(CommandContext commandContext)
    {
        Optional<UUID> senderUUID = commandContext.cause().audience().get(Identity.UUID);
        if  (senderUUID.isPresent()) {
            WSPlayer commandSender = CommonClass.server.getWSPlayer(senderUUID.get());
            WSPlayer subjectedPlayer = new SpongePlayer(commandContext.requireOne(playerParam));

            String waypointIdentifier = commandContext.requireOne(waypointParameter);

            ServerCommands.share(commandSender, subjectedPlayer, waypointIdentifier, ObjectType.WAYPOINT);

            return CommandResult.success();
        }

        return notValidUser();
    }

    private static CommandResult shareGroup(CommandContext commandContext)
    {
        Optional<UUID> senderUUID = commandContext.cause().audience().get(Identity.UUID);
        if (senderUUID.isPresent()) {
            WSPlayer commandSender = CommonClass.server.getWSPlayer(senderUUID.get());
            WSPlayer subjectedPlayer = new SpongePlayer(commandContext.requireOne(playerParam));

            String groupIdentifier = commandContext.requireOne(groupParameter);

            ServerCommands.share(commandSender, subjectedPlayer, groupIdentifier, ObjectType.GROUP);

            return CommandResult.success();
        }

        return notValidUser();
    }

    private static CommandResult stopSharingWaypoint(CommandContext commandContext)
    {
        Optional<UUID> senderUUID = commandContext.cause().audience().get(Identity.UUID);
        if (senderUUID.isPresent()) {
            WSPlayer commandSender = CommonClass.server.getWSPlayer(senderUUID.get());
            String identifier = commandContext.requireOne(waypointParameter);

            ServerCommands.removeShare(commandSender, identifier, ObjectType.WAYPOINT);

            return CommandResult.success();
        }

        return notValidUser();
    }

    private static CommandResult stopSharingGroup(CommandContext commandContext)
    {
        Optional<UUID> senderUUID = commandContext.cause().audience().get(Identity.UUID);
        if (senderUUID.isPresent()) {
            WSPlayer commandSender = CommonClass.server.getWSPlayer(senderUUID.get());
            String identifier = commandContext.requireOne(waypointParameter);

            ServerCommands.removeShare(commandSender, identifier, ObjectType.GROUP);

            return CommandResult.success();
        }

        return notValidUser();
    }
}
