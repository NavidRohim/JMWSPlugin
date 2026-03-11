package me.brynview.navidrohim.sponge;

import me.brynview.navidrohim.common.Constants;
import me.brynview.navidrohim.sponge.impl.SpongePlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.network.ClientSideConnection;
import org.spongepowered.api.network.EngineConnection;
import org.spongepowered.api.network.EngineConnectionState;
import org.spongepowered.api.network.ServerSideConnection;
import org.spongepowered.api.network.channel.ChannelBuf;
import org.spongepowered.api.network.channel.raw.play.RawPlayDataChannel;
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
