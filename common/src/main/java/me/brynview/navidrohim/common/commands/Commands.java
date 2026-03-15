package me.brynview.navidrohim.common.commands;

import java.util.List;

public class Commands {
    public static final List<Command> commands = List.of(
            new Command(
                    "share_waypoint",
                    SharingCommands::doWpShareArgPrep,
                    new PlayerProfileArgument("username"),
                    SharingCommands.waypointArgument()
            ),
            new Command(
                    "share_group",
                    SharingCommands::doGpShareArgPrep,
                    new PlayerProfileArgument("username"),
                    SharingCommands.groupArgument()
            ),
            new Command(
                    "stop_sharing_group",
                    SharingCommands::stopSharingGp,
                    SharingCommands.sharedGroupArgument()
            ),
            new Command(
                    "stop_sharing_waypoint",
                    SharingCommands::stopSharingWp,
                    SharingCommands.sharedWaypointArgument()
            )
    );

    public static final List<Command> adminCommands = List.of(
            new Command(
                    "create_global_waypoint",
                    SharingCommands::createServerWp,
                    SharingCommands.waypointArgument()
            ),
            new Command(
                    "create_global_group",
                    SharingCommands::createServerGp,
                    SharingCommands.groupArgument()
            ),
            new Command(
                    "remove_global_group",
                    SharingCommands::removeServerGp,
                    SharingCommands.globalGroupArgument()
            ),
            new Command(
                    "remove_global_waypoint",
                    SharingCommands::removeServerWp,
                    SharingCommands.globalWaypointArgument()
            )
    );
}
