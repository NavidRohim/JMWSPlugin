package me.brynview.navidrohim.common;

import me.brynview.navidrohim.common.api.WSPacket;
import me.brynview.navidrohim.common.api.WSPlayer;
import me.brynview.navidrohim.common.network.packets.ActionPacket;
import me.brynview.navidrohim.common.network.packets.HandshakePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Constants {

    private static class LoggerHolder {
        private static final Logger INSTANCE = LoggerFactory.getLogger(MODID);
    }

    public static Logger getLogger() {
        return LoggerHolder.INSTANCE;
    }

    public static final String MODID = "jmws";
    public static final String VERSION = "1.2.3-1.21.11-alpha.2"; // This is purely for display and is not needed
    public static final double SERVER_VERSION = 1.102;
    public static final boolean DEBUG = VERSION.contains("-beta.") || VERSION.contains("-alpha.");

    public static final int PACKET_SIZE = 2_097_000;
}
