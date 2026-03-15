package me.brynview.navidrohim.common.commands;

import me.brynview.navidrohim.common.Constants;
import me.brynview.navidrohim.common.api.server.WSPlayer;
import me.brynview.navidrohim.common.config.ServerConfig;
import me.brynview.navidrohim.common.enums.JMWSMessageType;
import me.brynview.navidrohim.common.enums.ObjectType;
import me.brynview.navidrohim.common.io.JMWSServerIO;
import me.brynview.navidrohim.common.network.PlayerNetworkingHelper;
import me.brynview.navidrohim.common.objects.ServerObject;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;

public class ServerCommands {
    public static void share(WSPlayer sender, WSPlayer player, String waypointID, ObjectType objectType) {
        if (ServerConfig.serverConfig.sharingEnabled)
        {
            if (sender.equals(player) && !Constants.DEBUG)
            {
                PlayerNetworkingHelper.sendUserMessage(sender, "sharing.jmws.cannot_share", true, false);
            } else {
                HashMap<String, Path> userObjs = JMWSServerIO.getNameHashmapLookup(sender.getUUID(), objectType);
                Path specifiedObj = userObjs.get(waypointID);
                if (specifiedObj != null)
                {
                    ServerObject objIns = JMWSServerIO.getObjectFromFile(specifiedObj, sender.getUUID(), objectType);
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

    public static int removeShare(WSPlayer sender, String waypointID, ObjectType objectType) {
        HashMap<String, Path> userObjPaths = JMWSServerIO.getNameHashmapLookup(sender.getUUID(), objectType);
        Path specifiedObj = userObjPaths.get(waypointID);

        if (specifiedObj != null)
        {
            ServerObject objIns = JMWSServerIO.getObjectFromFile(specifiedObj, sender.getUUID(), objectType);
            objIns.stopSharing();
            PlayerNetworkingHelper.sendUserMessage(sender, "sharing.jmws.stopped_sharing", true, JMWSMessageType.NEUTRAL);
        }
        else {
            PlayerNetworkingHelper.sendUserMessage(sender, "sharing.jmws.no_matching_object", true, JMWSMessageType.FAILURE);
        }

        return 1;
    }

    public static void globalShare(String objectName, WSPlayer player, ObjectType objectType, boolean make)
    {
        HashMap<String, Path> userObjs = JMWSServerIO.getNameHashmapLookup(player.getUUID(), objectType);
        @Nullable Path specifiedObject = userObjs.get(objectName);
        ServerObject globalObject = JMWSServerIO.getObjectFromFile(specifiedObject, player.getUUID(), objectType);

        if (specifiedObject != null && globalObject != null)
        {
            if (make)
            {
                globalObject.makeGlobal();
                PlayerNetworkingHelper.sendUserMessage(player, "global.jmws.made_global", true, JMWSMessageType.NEUTRAL);
            } else {
                if (globalObject.syncing.isGlobal())
                {
                    globalObject.removeGlobal();
                    PlayerNetworkingHelper.sendUserMessage(player, "global.jmws.remove_global", true, JMWSMessageType.NEUTRAL);
                } else {
                    PlayerNetworkingHelper.sendUserMessage(player, "global.jmws.not_global", true, JMWSMessageType.NEUTRAL);
                }
            }
        } else {
            PlayerNetworkingHelper.sendUserMessage(player, "sharing.jmws.no_matching_object", true, JMWSMessageType.FAILURE);
        }
    }
}
