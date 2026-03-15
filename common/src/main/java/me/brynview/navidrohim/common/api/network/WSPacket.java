package me.brynview.navidrohim.common.api.network;

import me.brynview.navidrohim.common.api.server.WSPlayer;

public interface WSPacket {
    byte[] encode();
    WSPacket send();
    WSPlayer getRecipient();

    String getChannel();

}
