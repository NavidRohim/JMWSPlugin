package me.brynview.navidrohim.common.enums;

import me.brynview.navidrohim.common.io.JMWSServerIO;
import me.brynview.navidrohim.common.objects.ServerGroup;
import me.brynview.navidrohim.common.objects.ServerObject;
import me.brynview.navidrohim.common.objects.ServerWaypoint;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public enum ObjectType {
    WAYPOINT(ServerWaypoint.class, "./jmws/"),
    GROUP(ServerGroup.class, "./jmws/groups/"),
    SHARED(null, "./jmws/users/"),
    GENERIC(null, null);

    private final Class<? extends ServerObject> savedClass;
    private final String objectPathPrefix;

    ObjectType(final Class<? extends ServerObject> savedClass, String objectPathPrefix) {
        this.savedClass = savedClass;
        this.objectPathPrefix = objectPathPrefix;
    }

    public static String getPathLocationPrefix(ObjectType objectType)
    {
        return objectType.getObjectPathPrefix();
    }

    public Class<? extends ServerObject> getObjectClass() {
        return savedClass;
    }

    public String getObjectPathPrefix()
    {
        return this.objectPathPrefix;
    }

    public <T extends ServerObject> Optional<T> getServerObject(String nonDuplicateIdentifier, UUID senderUUID)
    {
        HashMap<String, Path> userObjPaths = JMWSServerIO.getNameHashmapLookup(senderUUID, this);
        Path specifiedObj = userObjPaths.get(nonDuplicateIdentifier);

        if (specifiedObj != null) {
            return Optional.of(JMWSServerIO.getObjectFromFile(specifiedObj, senderUUID, this));
        } else {
            return Optional.empty();
        }
    }
}