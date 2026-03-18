package me.brynview.navidrohim.sponge.commands.impl;

import me.brynview.navidrohim.common.enums.ObjectType;
import me.brynview.navidrohim.sponge.commands.api.SpongeArgumentType;
import me.brynview.navidrohim.sponge.commands.api.SpongeCommandCommonEncoder;
import org.spongepowered.api.command.parameter.Parameter;

import java.util.Optional;
import java.util.UUID;

import static me.brynview.navidrohim.sponge.commands.impl.ImplUtil.withCompletionListOfObjects;
import static me.brynview.navidrohim.sponge.commands.impl.ImplUtil.withCompletionListOfSharedObjects;

public class SharedWaypointCommandCommonEncoder implements SpongeCommandCommonEncoder {

    private static Parameter.Value.Builder<String> sharedWaypoints() {
        return withCompletionListOfSharedObjects(ObjectType.WAYPOINT);
    }

    @Override
    public SpongeArgumentType<?> buildParameterForNative() {
        return new SpongeArgumentType<>(String.class, SharedWaypointCommandCommonEncoder::sharedWaypoints);
    }

    @Override
    public Optional<?> getCommonParameterValue(Object value, UUID commandSenderUUID) {
        return ObjectType.WAYPOINT.getServerObject(value.toString(), commandSenderUUID);
    }
}
