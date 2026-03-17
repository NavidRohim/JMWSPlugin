package me.brynview.navidrohim.sponge.commands.api;

import me.brynview.navidrohim.common.api.commands.ArgumentTypes;
import me.brynview.navidrohim.sponge.commands.SpongeCommands;

import java.util.Optional;
import java.util.UUID;

public interface SpongeCommandCommonEncoder {
    SpongeArgumentType<?> buildParameterForNative();
    Optional<?> getCommonParameterValue(Object value, UUID commandSenderUUID);
    ArgumentTypes getType();
}
