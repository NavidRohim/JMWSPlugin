package me.brynview.navidrohim.common.api.network;

import me.brynview.navidrohim.common.api.server.WSPlayer;

public interface WSNetworkHandler {
    WSPlayer getNetworkOwner();
    void sendHandshake();
    void sendPacket(String channel, byte[] packetData);
    void sendPacket(String channel, WSPacket packet);

}
