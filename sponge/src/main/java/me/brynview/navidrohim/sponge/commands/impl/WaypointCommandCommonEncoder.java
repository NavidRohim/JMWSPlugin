package me.brynview.navidrohim.sponge.commands.impl;

import me.brynview.navidrohim.common.api.commands.ArgumentTypes;
import me.brynview.navidrohim.common.enums.ObjectType;
import me.brynview.navidrohim.common.objects.ServerWaypoint;
import me.brynview.navidrohim.sponge.commands.api.SpongeArgumentType;
import me.brynview.navidrohim.sponge.commands.api.SpongeCommandCommonEncoder;
import org.spongepowered.api.command.parameter.Parameter;

import java.util.Optional;
import java.util.UUID;

import static me.brynview.navidrohim.sponge.commands.SpongeCommands.withCompletionListOf;

public class WaypointCommandCommonEncoder implements SpongeCommandCommonEncoder {

    private static Parameter.Value.Builder<String> waypoint() {
        return withCompletionListOf(ObjectType.WAYPOINT);
    }

    @Override
    public SpongeArgumentType<?> buildParameterForNative() {
        return new SpongeArgumentType<>(String.class, WaypointCommandCommonEncoder::waypoint);
    }

    @Override
    public Optional<ServerWaypoint> getCommonParameterValue(Object value, UUID commandSenderUUID) {
        return ObjectType.WAYPOINT.getServerObject(value.toString(), commandSenderUUID);
    }

    @Override
    public ArgumentTypes getType() {
        return ArgumentTypes.WAYPOINT;
    }
}
