package me.brynview.navidrohim.common.api.commands;

import me.brynview.navidrohim.common.api.game.WSPlayer;
import me.brynview.navidrohim.common.commands.ServerCommands;
import me.brynview.navidrohim.common.exceptions.CommandException;
import me.brynview.navidrohim.common.objects.ServerGroup;
import me.brynview.navidrohim.common.objects.ServerWaypoint;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public record CommonCommandContext(HashMap<String, Optional<?>> arguments, WSPlayer commandSender)
{

    private <T> T getArgumentAsRequired(String key, Class<T> clazz, String errorText) throws CommandException {
        Optional<T> value = getArgumentAsType(key, clazz);
        if (value.isPresent()) {
            return value.get();
        } else {
            throw new CommandException(errorText);
        }
    }

    public <T> Optional<T> getArgumentAsType(String key, Class<T> clazz) {
        Optional<?> value = arguments.get(key);

        if (value.isPresent()) {
            Object valueObj = value.get();
            return Optional.of(clazz.cast(valueObj));
        }

        return Optional.empty();
    }

    public ServerWaypoint getWaypoint(String key) throws CommandException
    {
        return getArgumentAsRequired(key, ServerWaypoint.class, "Could not find waypoint for key: " + key);
    }

    public ServerGroup getGroup(String key) throws CommandException
    {
        return getArgumentAsRequired(key, ServerGroup.class, "Could not find group for key: " + key);
    }

    public WSPlayer getPlayer(String key) throws CommandException {
        return getArgumentAsRequired(key, WSPlayer.class, "Could not find player for key: " + key);
    }

    public Optional<WSPlayer> getPlayerOptional(String key) {
        return getArgumentAsType(key, WSPlayer.class);
    }

    public Optional<ServerWaypoint> getWaypointOptional(String key) {
        return getArgumentAsType(key, ServerWaypoint.class);
    }

    public Optional<ServerGroup> getGroupOptional(String key) {
        return getArgumentAsType(key, ServerGroup.class);
    }
}
