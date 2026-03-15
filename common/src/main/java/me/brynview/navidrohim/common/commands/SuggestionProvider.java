package me.brynview.navidrohim.common.commands;

import me.brynview.navidrohim.common.enums.ObjectType;
import me.brynview.navidrohim.common.io.JMWSServerIO;
import me.brynview.navidrohim.common.objects.ServerObject;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SuggestionProvider {
    public static HashMap<String, ServerObject> getUserObjectsAsNameHashmap(UUID playerUUID, ObjectType objectType, boolean global, boolean onlyShared)
    {
        HashMap<String, ServerObject> stringServerObjectHashMap = new HashMap<>();
        for (ServerObject object : JMWSServerIO.getObjectsForUser(playerUUID, objectType, global))
        {
            String nonDupeIdentifier = object.getObjectNonDuplicateIdentifier();
            if ((!object.syncing.isGlobal() && !onlyShared) || global || (onlyShared && !object.syncing.sharedTo.isEmpty()))
            {
                stringServerObjectHashMap.put(nonDupeIdentifier, object);
            }
        }
        return stringServerObjectHashMap;
    }

    public static List<String> suggestWaypoints(UUID playerId)
    {
        return getUserObjectsAsNameHashmap(playerId, ObjectType.WAYPOINT, false, false).keySet().stream().toList();
    }

    public static List<String> suggestGroups(UUID playerId)
    {
        return getUserObjectsAsNameHashmap(playerId, ObjectType.GROUP, false, false).keySet().stream().toList();
    }

    public static List<String> suggestSharedGroups(UUID playerId)
    {
        return getUserObjectsAsNameHashmap(playerId, ObjectType.GROUP, false, true).keySet().stream().toList();
    }

    public static List<String> suggestSharedWaypoints(UUID playerId)
    {
        return getUserObjectsAsNameHashmap(playerId, ObjectType.WAYPOINT, false, true).keySet().stream().toList();
    }

    public static List<String> suggestGlobalWaypoints(UUID playerId)
    {
        return getUserObjectsAsNameHashmap(playerId, ObjectType.WAYPOINT, true, false).keySet().stream().toList();
    }

    public static List<String> suggestGlobalGroups(UUID playerId)
    {
        return getUserObjectsAsNameHashmap(playerId, ObjectType.GROUP, true, false).keySet().stream().toList();
    }
}
