package me.brynview.navidrohim.sponge.network;

import me.brynview.navidrohim.sponge.JMWSSponge;
import me.brynview.navidrohim.sponge.impl.SpongePlayer;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.network.EngineConnectionState;
import org.spongepowered.api.network.channel.ChannelBuf;
import org.spongepowered.api.network.channel.raw.play.RawPlayDataHandler;

public class MessageHandler implements RawPlayDataHandler<EngineConnectionState.Game> {
    private final String channel;

    public MessageHandler(String channel) {
        this.channel = channel;
    }

    @Override
    public void handlePayload(ChannelBuf data, EngineConnectionState.Game state) {
        JMWSSponge.getPlugin().onPluginMessage(channel, new SpongePlayer((ServerPlayer) state.player()), data.readBytes(data.capacity()));
    }
}
