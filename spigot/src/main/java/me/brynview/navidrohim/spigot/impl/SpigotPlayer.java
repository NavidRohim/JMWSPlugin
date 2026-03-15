package me.brynview.navidrohim.spigot.impl;

import me.brynview.navidrohim.common.CommonClass;
import me.brynview.navidrohim.common.api.command.ArgumentCasterRegistry;
import me.brynview.navidrohim.spigot.JMWSSpigot;
import me.brynview.navidrohim.common.api.server.WSPlayer;
import me.brynview.navidrohim.common.api.network.WSNetworkHandler;
import me.brynview.navidrohim.common.api.server.WSServer;
import me.brynview.navidrohim.common.network.packets.ActionPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpigotPlayer implements WSPlayer
{
    static {
        ArgumentCasterRegistry.register(SpigotPlayer.class, SpigotPlayer::cast);
    }

    private final Player nativePayerObj;
    private final SpigotServer server;

    private final SpigotNetworkHandler spigotNetworkHandler;

    public SpigotPlayer(@Nullable Player nativePlayerObj)
    {
        this.server = JMWSSpigot.server;
        this.nativePayerObj = nativePlayerObj;
        this.spigotNetworkHandler = new SpigotNetworkHandler(this, nativePlayerObj);
    }

    @Override
    public boolean equals(Object o)
    {
        return (this.getClass().equals(o.getClass()) && this.getUUID().equals(((SpigotPlayer) o).getUUID()));
    }

    @Override
    public WSNetworkHandler getNetworkHandler() {
        return this.spigotNetworkHandler;
    }

    @Override
    public WSServer getServer() {
        return this.server;
    }

    @Override
    public String getName() {
        return this.nativePayerObj.getDisplayName();
    }

    @Override
    public UUID getUUID() {
        return nativePayerObj.getUniqueId();
    }

    @Override
    public void sendHandshake()
    {
        Bukkit.getScheduler().runTaskLater(JMWSSpigot.getPluginInstance(), this.getNetworkHandler()::sendHandshake, 10);
    }

    @Override
    public void sendActionCommand(ActionPacket command) {
        this.getNetworkHandler().sendPacket(command.getChannel(), command.encode());
    }

    public static WSPlayer cast(Object value) {
        if (value instanceof PlayerProfile) {
            PlayerProfile profile = (PlayerProfile) value;
            return CommonClass.server.getWSPlayer(profile.getUniqueId());
        }
        throw new RuntimeException("SpigotPlayer cast only can be cast from PlayerProfile");
    }
}
