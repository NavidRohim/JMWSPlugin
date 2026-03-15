package me.brynview.navidrohim.sponge.impl;

import me.brynview.navidrohim.common.api.network.WSNetworkHandler;
import me.brynview.navidrohim.common.api.network.WSPacket;
import me.brynview.navidrohim.common.api.server.WSPlayer;
import me.brynview.navidrohim.common.network.packets.HandshakePacket;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.network.channel.raw.RawDataChannel;

public class SpongeNetworkHandler implements WSNetworkHandler {

    public final ServerPlayer playerObj;
    public final WSPlayer wsPlayer;

    SpongeNetworkHandler(ServerPlayer player, WSPlayer wsPlayer)
    {
        this.playerObj = player;
        this.wsPlayer = wsPlayer;
    }

    @Override
    public WSPlayer getNetworkOwner() {
        return this.wsPlayer;
    }

    @Override
    public void sendHandshake() {
        new HandshakePacket(this.getNetworkOwner()).send();
    }

    @Override
    public void sendPacket(String channel, byte[] packetData)
    {
        String[] key = channel.split(":");
        Sponge.channelManager().ofType(
                ResourceKey.of(key[0], key[1]),
                RawDataChannel.class
        ).play().sendTo(this.playerObj, channelBuf ->  channelBuf.writeBytes(packetData));
    }

    @Override
    public void sendPacket(String channel, WSPacket packet) {
        this.sendPacket(packet.getChannel(), packet.encode());
    }
}
