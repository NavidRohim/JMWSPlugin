package me.brynview.navidrohim.sponge.impl;

import me.brynview.navidrohim.common.api.network.WSNetworkHandler;
import me.brynview.navidrohim.common.api.server.WSPlayer;
import me.brynview.navidrohim.common.api.server.WSServer;
import me.brynview.navidrohim.common.network.packets.ActionPacket;
import me.brynview.navidrohim.sponge.JMWSSponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.UUID;

public class SpongePlayer implements WSPlayer {

    public final Player nativePlayer;
    public final WSServer server;
    public final SpongeNetworkHandler networkHandler;

    public SpongePlayer(ServerPlayer nativePlayer) {
        this.nativePlayer = nativePlayer;
        this.server = JMWSSponge.commonServer;
        this.networkHandler = new SpongeNetworkHandler(nativePlayer, this);
    }

    @Override
    public String getName() {
        return this.nativePlayer.name();
    }

    @Override
    public UUID getUUID() {
        return this.nativePlayer.uniqueId();
    }

    @Override
    public void sendHandshake() {
        this.networkHandler.sendHandshake();
    }

    @Override
    public void sendActionCommand(ActionPacket command)
    {
        this.getNetworkHandler().sendPacket(command.getChannel(), command);
    }

    @Override
    public WSNetworkHandler getNetworkHandler() {
        return this.networkHandler;
    }

    @Override
    public WSServer getServer() {
        return this.server;
    }
}
