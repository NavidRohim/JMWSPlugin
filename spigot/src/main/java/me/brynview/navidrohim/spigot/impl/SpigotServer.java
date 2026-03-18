package me.brynview.navidrohim.spigot.impl;

import me.brynview.navidrohim.spigot.JMWSSpigot;
import me.brynview.navidrohim.common.api.networking.PacketFlow;
import me.brynview.navidrohim.common.api.game.WSPlayer;
import me.brynview.navidrohim.common.api.game.WSServer;
import me.brynview.navidrohim.common.network.packets.ActionPacket;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpigotServer implements WSServer {
    public static Server server = null;

    public SpigotServer(Server server)
    {
        if (SpigotServer.server == null)
        {
            SpigotServer.server = server;
        } else {
            throw new RuntimeException("Cannot initialise more than one instance of SpigotServer.");
        }
    }


    @Override
    public void sendActionCommandToClient(UUID uuid, ActionPacket packet)
    {
        SpigotPlayer player = this.getWSPlayer(uuid);
        player.sendActionCommand(packet);

    }

    @Override
    public void sendActionCommandToClient(UUID uuid, String packetEncodable)
    {
        SpigotPlayer player = this.getWSPlayer(uuid);
        new ActionPacket(packetEncodable, player).send();
    }

    @Override
    public void registerPacket(PacketFlow direction, String channel)
    {
        JMWSSpigot plugin = JMWSSpigot.getPluginInstance();
        Messenger messenger = JMWSSpigot.server.getNativeServer().getMessenger();

        if (direction == PacketFlow.OUTGOING)
        {
            messenger.registerOutgoingPluginChannel(plugin, channel);
        } else if  (direction == PacketFlow.INCOMING)
        {
            messenger.registerIncomingPluginChannel(plugin, channel, plugin);
        }
    }

    @Override
    public SpigotPlayer getWSPlayer(UUID uuid)
    {
        return new SpigotPlayer(this.getNativeServer().getPlayer(uuid));
    }

    @Override
    @Nullable
    public WSPlayer getWSPlayer(String name) {
        return new SpigotPlayer(this.getNativeServer().getPlayerExact(name));
    }

    public Server getNativeServer()
    {
        return server;
    }
}
