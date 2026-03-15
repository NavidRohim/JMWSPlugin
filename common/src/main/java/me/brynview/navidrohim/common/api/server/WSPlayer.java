package me.brynview.navidrohim.common.api.server;

import me.brynview.navidrohim.common.api.network.WSNetworkHandler;
import me.brynview.navidrohim.common.network.packets.ActionPacket;

import java.util.UUID;

public interface WSPlayer {

    @Override
    boolean equals(Object obj);

    String getName();
    UUID getUUID();

    void sendHandshake();
    void sendActionCommand(ActionPacket command);

    WSNetworkHandler getNetworkHandler();
    WSServer getServer();
}
