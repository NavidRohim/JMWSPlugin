package me.brynview.navidrohim.sponge.commands.impl;

import me.brynview.navidrohim.common.api.commands.ArgumentTypes;
import me.brynview.navidrohim.common.api.game.WSPlayer;
import me.brynview.navidrohim.sponge.commands.api.SpongeArgumentType;
import me.brynview.navidrohim.sponge.commands.api.SpongeCommandCommonEncoder;
import me.brynview.navidrohim.sponge.impl.game.SpongePlayer;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public class PlayerCommandCommonEncoder implements SpongeCommandCommonEncoder {

    @Override
    public SpongeArgumentType<?> buildParameterForNative() {
        return new SpongeArgumentType<>(ServerPlayer.class, Parameter::player);
    }

    @Override
    public Optional<WSPlayer> getCommonParameterValue(Object value, UUID commandSenderUUID) {
        if (value instanceof ServerPlayer) {
            return Optional.of(new SpongePlayer((ServerPlayer) value));
        }
        return Optional.empty();
    }

    @Override
    public ArgumentTypes getType() {
        return ArgumentTypes.PLAYER;
    }
}
