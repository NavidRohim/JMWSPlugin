package me.brynview.navidrohim.sponge.commands.impl;

import me.brynview.navidrohim.common.api.commands.ArgumentTypes;
import me.brynview.navidrohim.common.enums.ObjectType;
import me.brynview.navidrohim.common.objects.ServerGroup;
import me.brynview.navidrohim.sponge.commands.api.SpongeArgumentType;
import me.brynview.navidrohim.sponge.commands.api.SpongeCommandCommonEncoder;
import org.spongepowered.api.command.parameter.Parameter;

import java.util.Optional;
import java.util.UUID;

import static me.brynview.navidrohim.sponge.commands.SpongeCommands.withCompletionListOf;

public class GroupCommandCommonEncoder implements SpongeCommandCommonEncoder {

    private static Parameter.Value.Builder<String> group() {
        return withCompletionListOf(ObjectType.GROUP);
    }

    @Override
    public SpongeArgumentType<?> buildParameterForNative() {
        return new SpongeArgumentType<>(String.class, GroupCommandCommonEncoder::group);
    }

    @Override
    public Optional<ServerGroup> getCommonParameterValue(Object value, UUID commandSenderUUID) {
        return ObjectType.GROUP.getServerObject(value.toString(), commandSenderUUID);
    }

    @Override
    public ArgumentTypes getType() {
        return ArgumentTypes.GROUP;
    }
}
