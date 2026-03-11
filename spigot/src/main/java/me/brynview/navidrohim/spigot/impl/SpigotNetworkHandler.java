package me.brynview.navidrohim.spigot.impl;

import me.brynview.navidrohim.spigot.JMWSSpigot;
import me.brynview.navidrohim.common.api.WSPacket;
import me.brynview.navidrohim.common.api.WSPlayer;
import me.brynview.navidrohim.common.api.WSNetworkHandler;
import org.bukkit.entity.Player;

public class SpigotNetworkHandler implements WSNetworkHandler {


    private final WSPlayer networkOwner;
    private final Player player;

    public SpigotNetworkHandler(WSPlayer networkOwner, Player owner)
    {
        this.networkOwner = networkOwner;
        this.player = owner;

    }

    @Override
    public WSPlayer getNetworkOwner() {
        return this.networkOwner;
    }

    @Override
    public void sendHandshake()
    {
        this.getNetworkOwner().sendHandshake();
    }

    public void sendPacket(String channel, byte[] packetData)
    {
        this.player.sendPluginMessage(JMWSSpigot.getPluginInstance(), channel, packetData);
    }

    @Override
    public void sendPacket(String channel, WSPacket packet)
    {
        this.player.sendPluginMessage(JMWSSpigot.getPluginInstance(), channel, packet.encode());
    }
}
