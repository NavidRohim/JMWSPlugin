package me.brynview.navidrohim.common.network;

import me.brynview.navidrohim.common.api.server.WSPlayer;
import me.brynview.navidrohim.common.enums.JMWSMessageType;
import me.brynview.navidrohim.common.helper.CommandFactory;
import me.brynview.navidrohim.common.network.packets.ActionPacket;

public class PlayerNetworkingHelper {
    public static void sendUserMessage(WSPlayer player, String messageKey, Boolean overlay, boolean isError, boolean silent) {
        if (!silent) // is this dumb
        {
            ActionPacket packet = new ActionPacket(CommandFactory.makeClientAlertRequestJson(messageKey, overlay, isError ? JMWSMessageType.FAILURE : JMWSMessageType.NEUTRAL), player);
            player.sendActionCommand(packet);
        }
    }

    public static void sendUserMessage(WSPlayer player, String messageKey, Boolean overlay, boolean isError) {
        sendUserMessage(player, messageKey, overlay, isError, false);
    }

    public static void sendUserMessage(WSPlayer player, String messageKey, Boolean overlay, JMWSMessageType messageType)
    {
        new ActionPacket(CommandFactory.makeClientAlertRequestJson(messageKey, overlay, messageType), player).send();
    }

    /* Will need server layer for this
    public static void sendUserMessage(UUID player, String messageKey, Boolean overlay, JMWSMessageType messageType) {
        JMWSActionPayload messagePayload = new JMWSActionPayload(CommandFactory.makeClientAlertRequestJson(messageKey, overlay, messageType));
        Dispatcher.sendToClient(messagePayload, CommonClass.getMinecraftServerInstance().getPlayerList().getPlayer(player));
    }*/
}
