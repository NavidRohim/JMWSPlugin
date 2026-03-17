package me.brynview.navidrohim.common.api.game;

import me.brynview.navidrohim.common.api.networking.PacketFlow;
import me.brynview.navidrohim.common.network.packets.ActionPacket;

import java.util.UUID;

public interface WSServer {
    void sendActionCommandToClient(UUID uuid, ActionPacket packet);
    void sendActionCommandToClient(UUID uuid, String packetEncodable);
    void registerPacket(PacketFlow direction, String channel);

    WSPlayer getWSPlayer(UUID uuid);
    WSPlayer getWSPlayer(String name);
    void teleportPlayer(UUID uuid, int x, int y, int z);
}
