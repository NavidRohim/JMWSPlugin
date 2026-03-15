package me.brynview.navidrohim.common.events;

import me.brynview.navidrohim.common.api.server.WSPlayer;

public class CommonEvents {

    public static void handleJoin(WSPlayer serverPlayer)
    {
        serverPlayer.sendHandshake();
    }
}
