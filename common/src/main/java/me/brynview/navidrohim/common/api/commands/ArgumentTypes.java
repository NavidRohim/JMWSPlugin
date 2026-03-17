package me.brynview.navidrohim.common.api.commands;

import me.brynview.navidrohim.common.api.game.WSPlayer;
import me.brynview.navidrohim.common.enums.ObjectType;
import me.brynview.navidrohim.common.objects.ServerGroup;
import me.brynview.navidrohim.common.objects.ServerObject;
import me.brynview.navidrohim.common.objects.ServerWaypoint;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public enum ArgumentTypes {
    WAYPOINT,
    GROUP,
    PLAYER
}
