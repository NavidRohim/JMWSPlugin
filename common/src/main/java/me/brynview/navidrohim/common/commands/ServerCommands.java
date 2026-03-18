package me.brynview.navidrohim.common.commands;

import me.brynview.navidrohim.common.Constants;
import me.brynview.navidrohim.common.api.commands.CommonCommand;
import me.brynview.navidrohim.common.api.commands.CommonCommandContext;
import me.brynview.navidrohim.common.api.game.WSPlayer;
import me.brynview.navidrohim.common.config.ServerConfig;
import me.brynview.navidrohim.common.enums.JMWSMessageType;
import me.brynview.navidrohim.common.enums.ObjectType;
import me.brynview.navidrohim.common.exceptions.CommandException;
import me.brynview.navidrohim.common.io.JMWSServerIO;
import me.brynview.navidrohim.common.network.PlayerNetworkingHelper;
import me.brynview.navidrohim.common.objects.ServerGroup;
import me.brynview.navidrohim.common.objects.ServerObject;
import me.brynview.navidrohim.common.objects.ServerWaypoint;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;

public class ServerCommands {

    private static void shareAny(WSPlayer sender, WSPlayer player, Optional<? extends ServerObject> shareableObject) {
        if (ServerConfig.serverConfig.sharingEnabled)
        {
            if (sender.equals(player))
            {
                PlayerNetworkingHelper.sendUserMessage(sender, "sharing.jmws.cannot_share", true, false);
            } else {
                if (shareableObject.isPresent())
                {
                    ServerObject objIns = shareableObject.get();
                    if (!objIns.syncing.isGlobal())
                    {
                        objIns.share(sender, player);
                    } else {
                        PlayerNetworkingHelper.sendUserMessage(sender, "sharing.jmws.cannot_share_global", true, JMWSMessageType.WARNING);
                    }
                }
                else {
                    PlayerNetworkingHelper.sendUserMessage(sender, "sharing.jmws.no_matching_object", true, JMWSMessageType.FAILURE);
                }
            }
        } else {
            PlayerNetworkingHelper.sendUserMessage(sender, "sharing.jmws.no_server_sharing", true, JMWSMessageType.FAILURE);
        }
    }

    public static void share(CommonCommandContext ctx) {

        try
        {
            WSPlayer sender = ctx.commandSender();
            WSPlayer player = ctx.getPlayer("player");
            Optional<ServerWaypoint> waypoint = ctx.getWaypointOptional("waypoint");

            shareAny(sender, player, waypoint);
        } catch (CommandException e) {
            Constants.getLogger().info("Failed to execute command, error message: " + e.getMessage());
        }
    }

    public static void shareGroup(CommonCommandContext ctx) {

        try
        {
            WSPlayer sender = ctx.commandSender();
            WSPlayer player = ctx.getPlayer("player");
            Optional<ServerGroup> group = ctx.getGroupOptional("waypoint");

            shareAny(sender, player, group);
        } catch (CommandException e) {
            Constants.getLogger().info("Failed to execute command, error message: " + e.getMessage());
        }
    }

    private static void removeShareFromShareable(WSPlayer sender, Optional<? extends ServerObject> shareableObject)
    {
        if (shareableObject.isPresent())
        {
            ServerObject objIns = shareableObject.get();
            objIns.stopSharing();
            PlayerNetworkingHelper.sendUserMessage(sender, "sharing.jmws.stopped_sharing", true, JMWSMessageType.NEUTRAL);
        }
        else {
            PlayerNetworkingHelper.sendUserMessage(sender, "sharing.jmws.no_matching_object", true, JMWSMessageType.FAILURE);
        }
    }

    public static void removeShareWaypoint(CommonCommandContext ctx) {
        WSPlayer sender = ctx.commandSender();
        Optional<ServerWaypoint> waypoint = ctx.getWaypointOptional("waypoint");

        removeShareFromShareable(sender, waypoint);
    }

    public static void removeShareGroup(CommonCommandContext ctx) {
        WSPlayer sender = ctx.commandSender();
        Optional<ServerGroup> group = ctx.getGroupOptional("group");

        removeShareFromShareable(sender, group);
    }

    private static void makeShareableObjectGlobal(WSPlayer sender, Optional<? extends ServerObject> shareableObject)
    {
        if (shareableObject.isPresent())
        {
            shareableObject.get().makeGlobal();
            PlayerNetworkingHelper.sendUserMessage(sender, "global.jmws.made_global", true, JMWSMessageType.NEUTRAL);
        } else {
            PlayerNetworkingHelper.sendUserMessage(sender, "sharing.jmws.no_matching_object", true, JMWSMessageType.FAILURE);
        }
    }

    private static void makeShareableObjectNonGlobal(WSPlayer sender, Optional<? extends ServerObject> shareableObject)
    {
        if (shareableObject.isPresent())
        {
            ServerObject globalObject = shareableObject.get();
            if (globalObject.syncing.isGlobal())
            {
                globalObject.removeGlobal();
                PlayerNetworkingHelper.sendUserMessage(sender, "global.jmws.remove_global", true, JMWSMessageType.NEUTRAL);
            } else {
                PlayerNetworkingHelper.sendUserMessage(sender, "global.jmws.not_global", true, JMWSMessageType.NEUTRAL);
            }
        } else {
            PlayerNetworkingHelper.sendUserMessage(sender, "sharing.jmws.no_matching_object", true, JMWSMessageType.FAILURE);
        }
    }

    public static void globalWaypoint(CommonCommandContext ctx) {
        makeShareableObjectGlobal(ctx.commandSender(), ctx.getWaypointOptional("waypoint"));
    }

    public static void globalGroup(CommonCommandContext ctx) {
        makeShareableObjectGlobal(ctx.commandSender(), ctx.getGroupOptional("group"));
    }

    public static void nonGlobalWaypoint(CommonCommandContext ctx) {
        makeShareableObjectNonGlobal(ctx.commandSender(), ctx.getWaypointOptional("waypoint"));
    }

    public static void nonGlobalGroup(CommonCommandContext ctx) {
        makeShareableObjectNonGlobal(ctx.commandSender(), ctx.getGroupOptional("group"));
    }
}
