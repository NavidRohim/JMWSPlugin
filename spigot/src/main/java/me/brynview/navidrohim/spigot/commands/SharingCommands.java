package me.brynview.navidrohim.spigot.commands;

import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import me.brynview.navidrohim.common.CommonClass;
import me.brynview.navidrohim.common.api.server.WSPlayer;
import me.brynview.navidrohim.common.commands.ServerCommands;
import me.brynview.navidrohim.common.commands.SuggestionProvider;
import me.brynview.navidrohim.common.enums.ObjectType;
import org.bukkit.command.CommandSender;
import org.bukkit.profile.PlayerProfile;

import java.util.ArrayList;

public class SharingCommands {

    private static void doGpShareArgPrep(CommandSender commandSender, CommandArguments commandArguments)
    {
        WSPlayer spigotPlayerSender = CommonClass.server.getWSPlayer(commandSender.getName());

        PlayerProfile toPlayerProfile = (PlayerProfile) commandArguments.get("username");
        WSPlayer toPlayer = CommonClass.server.getWSPlayer(toPlayerProfile.getUniqueId());

        String wpName = commandArguments.get("groupName").toString();

        ServerCommands.share(spigotPlayerSender, toPlayer, wpName, ObjectType.GROUP);
    }

    private static void doWpShareArgPrep(CommandSender commandSender, CommandArguments commandArguments)
    {
        WSPlayer spigotPlayerSender = CommonClass.server.getWSPlayer(commandSender.getName());
        // Following code makes me want to actually want to die
        WSPlayer toPlayer = CommonClass.server.getWSPlayer(((ArrayList<PlayerProfile>)commandArguments.get("username")).getFirst().getUniqueId());
        String wpName = commandArguments.get("waypointName").toString();

        ServerCommands.share(spigotPlayerSender, toPlayer, wpName, ObjectType.WAYPOINT);
    }

    private static void stopSharingGp(CommandSender commandSender, CommandArguments commandArguments)
    {
        WSPlayer spigotPlayerSender = CommonClass.server.getWSPlayer(commandSender.getName());
        String groupName = commandArguments.get("groupName").toString();

        ServerCommands.removeShare(spigotPlayerSender,  groupName, ObjectType.GROUP);
    }

    private static void stopSharingWp(CommandSender commandSender, CommandArguments commandArguments)
    {
        WSPlayer spigotPlayerSender = CommonClass.server.getWSPlayer(commandSender.getName());
        String waypointName = commandArguments.get("waypointName").toString();

        ServerCommands.removeShare(spigotPlayerSender, waypointName, ObjectType.WAYPOINT);
    }

    private static void createServerWp(CommandSender commandSender, CommandArguments commandArguments)
    {
        WSPlayer spigotPlayerSender = CommonClass.server.getWSPlayer(commandSender.getName());
        String waypointName = commandArguments.get("waypointName").toString();

        ServerCommands.globalShare(waypointName, spigotPlayerSender, ObjectType.WAYPOINT, true);
    }

    private static void createServerGp(CommandSender commandSender, CommandArguments commandArguments)
    {
        WSPlayer spigotPlayerSender = CommonClass.server.getWSPlayer(commandSender.getName());
        String waypointName = commandArguments.get("groupName").toString();

        ServerCommands.globalShare(waypointName, spigotPlayerSender, ObjectType.GROUP, true);
    }

    private static void removeServerWp(CommandSender commandSender, CommandArguments commandArguments)
    {
        WSPlayer spigotPlayerSender = CommonClass.server.getWSPlayer(commandSender.getName());
        String waypointName = commandArguments.get("waypointName").toString();

        ServerCommands.globalShare(waypointName, spigotPlayerSender, ObjectType.WAYPOINT, false);
    }

    private static void removeServerGp(CommandSender commandSender, CommandArguments commandArguments)
    {
        WSPlayer spigotPlayerSender = CommonClass.server.getWSPlayer(commandSender.getName());
        String waypointName = commandArguments.get("groupName").toString();

        ServerCommands.globalShare(waypointName, spigotPlayerSender, ObjectType.GROUP, false);
    }

    // Suggestions

    private static GreedyStringArgument waypointArgument()
    {
        GreedyStringArgument argument = new GreedyStringArgument("waypointName");
        argument.replaceSuggestions(ArgumentSuggestions.stringCollection(
                (cssi -> SuggestionProvider.suggestWaypoints(cssi.sender().getServer().getPlayer(cssi.sender().getName()).getUniqueId()))
        ));
        return argument;
    }

    private static GreedyStringArgument groupArgument()
    {
        GreedyStringArgument argument = new GreedyStringArgument("groupName");
        argument.replaceSuggestions(ArgumentSuggestions.stringCollection(
                (cssi -> SuggestionProvider.suggestGroups(cssi.sender().getServer().getPlayer(cssi.sender().getName()).getUniqueId()))
        ));
        return argument;
    }

    private static GreedyStringArgument sharedWaypointArgument()
    {
        GreedyStringArgument argument = new GreedyStringArgument("waypointName");
        argument.replaceSuggestions(ArgumentSuggestions.stringCollection(
                (cssi -> SuggestionProvider.suggestSharedWaypoints(cssi.sender().getServer().getPlayer(cssi.sender().getName()).getUniqueId()))
        ));
        return argument;
    }

    private static GreedyStringArgument sharedGroupArgument()
    {
        GreedyStringArgument argument = new GreedyStringArgument("groupName");
        argument.replaceSuggestions(ArgumentSuggestions.stringCollection(
                (cssi -> SuggestionProvider.suggestSharedGroups(cssi.sender().getServer().getPlayer(cssi.sender().getName()).getUniqueId()))
        ));
        return argument;
    }

    private static GreedyStringArgument globalWaypointArgument()
    {
        GreedyStringArgument argument = new GreedyStringArgument("waypointName");
        argument.replaceSuggestions(ArgumentSuggestions.stringCollection(
                (cssi -> SuggestionProvider.suggestGlobalWaypoints(cssi.sender().getServer().getPlayer(cssi.sender().getName()).getUniqueId()))
        ));
        return argument;
    }

    private static GreedyStringArgument globalGroupArgument()
    {
        GreedyStringArgument argument = new GreedyStringArgument("groupName");
        argument.replaceSuggestions(ArgumentSuggestions.stringCollection(
                (cssi -> SuggestionProvider.suggestGlobalGroups(cssi.sender().getServer().getPlayer(cssi.sender().getName()).getUniqueId()))
        ));
        return argument;
    }

    public static GreedyStringArgument greedyStringArgument(String name)
    {
        return new GreedyStringArgument(name);
    }
}
